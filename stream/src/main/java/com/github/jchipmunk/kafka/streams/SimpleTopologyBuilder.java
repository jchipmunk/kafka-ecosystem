package com.github.jchipmunk.kafka.streams;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jchipmunk.kafka.streams.core.TopologyBuilder;
import com.github.jchipmunk.kafka.streams.core.serialization.JsonSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.Stores;

import javax.annotation.Nonnull;
import java.util.Properties;

public class SimpleTopologyBuilder implements TopologyBuilder {

    @Nonnull
    @Override
    public Topology buildTopology(@Nonnull Properties props) {
        StreamsBuilder builder = new StreamsBuilder();

        builder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("messages-store"),
                        Serdes.String(),
                        Serdes.String()
                ));

        builder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore("persistent-messages-store"),
                        Serdes.String(),
                        Serdes.String()
                ));

        KStream<String, JsonNode> inputStream =
                builder.stream("input", Consumed.with(Serdes.String(), new JsonSerde()))
                        .filter((key, value) -> value.isObject());

        inputStream.process(StoreProcessor::new, "messages-store", "persistent-messages-store");

        inputStream.mapValues(SimpleTopologyBuilder::transform)
                .to("output", Produced.with(Serdes.String(), new JsonSerde()));

        return builder.build();
    }

    @Nonnull
    private static JsonNode transform(@Nonnull JsonNode value) {
        ObjectNode objectNode = value.deepCopy();
        return objectNode;
    }
}
