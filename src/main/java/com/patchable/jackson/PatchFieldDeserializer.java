package com.patchable.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.patchable.api.PatchField;

import java.io.IOException;

public class PatchFieldDeserializer extends JsonDeserializer<PatchField<?>> implements ContextualDeserializer {

    private JavaType valueType;

    public PatchFieldDeserializer() {}

    private PatchFieldDeserializer(JavaType valueType) {
        this.valueType = valueType;
    }

    @Override
    public PatchField<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentToken() == JsonToken.VALUE_NULL) {
            return PatchField.delete();
        }
        Object value = ctxt.readValue(p, valueType);
        return PatchField.of(value);
    }

    @Override
    public PatchField<?> getNullValue(DeserializationContext ctxt) {
        return PatchField.delete();
    }

    @Override
    public PatchField<?> getAbsentValue(DeserializationContext ctxt) {
        return PatchField.unset();
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        JavaType wrapperType = (property != null)
                ? property.getType()
                : ctxt.getContextualType();
        JavaType innerType = wrapperType.containedType(0);
        return new PatchFieldDeserializer(innerType);
    }
}
