package com.github.jchipmunk.kafka.streams.core.serialization;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import javax.annotation.Nonnull;
import java.util.Map;

abstract class AbstractSerde<T> implements Serde<T> {

    @Nonnull
    final private Serializer<T> serializer;
    @Nonnull
    final private Deserializer<T> deserializer;

    AbstractSerde(@Nonnull Serializer<T> serializer, @Nonnull Deserializer<T> deserializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public void configure(@Nonnull Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }

    @Override
    public void close() {
        serializer.close();
        deserializer.close();
    }

    @Nonnull
    @Override
    public Serializer<T> serializer() {
        return serializer;
    }

    @Nonnull
    @Override
    public Deserializer<T> deserializer() {
        return deserializer;
    }
}
