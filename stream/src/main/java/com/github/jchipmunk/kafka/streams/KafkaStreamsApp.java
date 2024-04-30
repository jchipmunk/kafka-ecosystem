package com.github.jchipmunk.kafka.streams;

import com.github.jchipmunk.kafka.streams.core.KafkaStreamsLauncher;
import com.github.jchipmunk.kafka.streams.core.TopologyBuilder;

import java.io.IOException;

public class KafkaStreamsApp {

    public static void main(String[] args) throws IOException {
        TopologyBuilder topologyBuilder = new SimpleTopologyBuilder();
        String pathToPropsFile = args[0];
        KafkaStreamsLauncher streamsLauncher = new KafkaStreamsLauncher(topologyBuilder, pathToPropsFile);
        streamsLauncher.run();
    }
}
