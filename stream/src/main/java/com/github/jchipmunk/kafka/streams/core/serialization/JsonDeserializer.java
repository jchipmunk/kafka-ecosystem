package com.github.jchipmunk.kafka.streams.core.serialization;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public class JsonDeserializer implements Deserializer<JsonNode> {

    @Nonnull
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonDeserializer() {
        this(Collections.emptySet(), new JsonNodeFactory(true), true);
    }

    JsonDeserializer(@Nonnull Set<DeserializationFeature> deserializationFeatures,
                     @Nonnull JsonNodeFactory jsonNodeFactory,
                     boolean enableModules) {
        objectMapper.enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature());
        deserializationFeatures.forEach(objectMapper::enable);
        objectMapper.setNodeFactory(jsonNodeFactory);
        if (enableModules) {
            objectMapper.findAndRegisterModules();
        }
    }

    @Override
    public JsonNode deserialize(@Nonnull String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.readTree(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }
}
