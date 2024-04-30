package com.github.jchipmunk.kafka.streams.core;

import org.apache.kafka.streams.Topology;

import javax.annotation.Nonnull;
import java.util.Properties;

public interface TopologyBuilder {

    @Nonnull
    Topology buildTopology(@Nonnull Properties props);
}
