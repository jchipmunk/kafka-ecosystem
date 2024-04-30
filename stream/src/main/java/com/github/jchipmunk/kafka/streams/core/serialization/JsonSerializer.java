package com.github.jchipmunk.kafka.streams.core.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

public class JsonSerializer implements Serializer<JsonNode> {

    @Nonnull
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonSerializer() {
        this(Collections.emptySet(), new JsonNodeFactory(true), true);
    }

    JsonSerializer(@Nonnull Set<SerializationFeature> serializationFeatures,
                   @Nonnull JsonNodeFactory jsonNodeFactory,
                   boolean enableModules) {
        serializationFeatures.forEach(objectMapper::enable);
        objectMapper.setNodeFactory(jsonNodeFactory);
        if (enableModules) {
            objectMapper.findAndRegisterModules();
        }
    }

    @Override
    public byte[] serialize(@Nonnull String topic, @Nullable JsonNode data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing JSON message", e);
        }
    }
}
