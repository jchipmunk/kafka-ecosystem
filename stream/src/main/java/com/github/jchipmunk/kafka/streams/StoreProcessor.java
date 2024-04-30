package com.github.jchipmunk.kafka.streams;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;

import javax.annotation.Nonnull;

public class StoreProcessor implements Processor<String, JsonNode, String, JsonNode> {

    @Nonnull
    private final String storeName;
    @Nonnull
    private final String persistentStoreName;

    private KeyValueStore<String, String> store;
    private KeyValueStore<String, String> persistentStore;

    public StoreProcessor() {
        this.storeName = "messages-store";
        this.persistentStoreName = "persistent-messages-store";
    }

    @Override
    public void init(ProcessorContext<String, JsonNode> context) {
        this.store = context.getStateStore(storeName);
        this.persistentStore = context.getStateStore(persistentStoreName);
    }

    @Override
    public void process(Record<String, JsonNode> record) {
        if (record.value() != null) {
            store.put(record.key(), record.value().toPrettyString());
            persistentStore.put(record.key(), record.value().toPrettyString());
        }
    }
}