package com.github.jchipmunk.kafka.streams.core;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.kafka.common.utils.Utils.loadProps;

public class KafkaStreamsLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsLauncher.class);

    @Nonnull
    private final AtomicReference<ApplicationState> state;
    @Nonnull
    private final Topology topology;
    @Nonnull
    private final Properties props;

    public KafkaStreamsLauncher(@Nonnull TopologyBuilder topologyBuilder,
                                @Nullable String pathToPropsFile) throws IOException {
        this(topologyBuilder, loadProps(pathToPropsFile));
    }

    public KafkaStreamsLauncher(@Nonnull TopologyBuilder topologyBuilder, @Nonnull Properties props) {
        state = new AtomicReference<>(ApplicationState.INITIALIZED);
        this.topology = topologyBuilder.buildTopology(props);
        LOGGER.info("Topology description:\n{}", topology.describe());
        this.props = props;
    }

    public void run() {
        try (KafkaStreams streams = new KafkaStreams(topology, props)) {
            CountDownLatch latch = new CountDownLatch(1);

            streams.setUncaughtExceptionHandler(exception -> {
                LOGGER.error("Uncaught exception handler triggered:", exception);
                state.set(ApplicationState.FAILED);
                latch.countDown();
                return null;
            });
            LOGGER.info("Uncaught exception handler added.");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("Shutdown hook triggered.");
                state.compareAndSet(ApplicationState.STARTED, ApplicationState.STOPPED);
                latch.countDown();
            }, "kafka-streams-shutdown-hook"));
            LOGGER.info("Shutdown hook added.");

            streams.start();
            state.compareAndSet(ApplicationState.INITIALIZED, ApplicationState.STARTED);
            latch.await();
        } catch (Throwable e) {
            LOGGER.error("Application encountered a fatal error:", e);
            state.set(ApplicationState.FAILED);
        }
        if (state.get() == ApplicationState.FAILED) {
            System.exit(1);
        }
    }
}
