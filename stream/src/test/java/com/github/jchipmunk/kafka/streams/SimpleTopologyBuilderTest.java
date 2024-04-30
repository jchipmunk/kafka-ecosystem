package com.github.jchipmunk.kafka.streams;

import com.github.jchipmunk.kafka.streams.core.TopologyBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.Test;

import java.util.Properties;

public class SimpleTopologyBuilderTest {

    @Test
    public void test() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");

        TopologyBuilder topologyBuilder = new SimpleTopologyBuilder();
        Topology topology = topologyBuilder.buildTopology(props);
        try (TopologyTestDriver testDriver = new TopologyTestDriver(topology, props)) {
        }
    }
}