package se499.kayaanbackend.Study_Group.entity;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class ContentTypeDeserializer extends JsonDeserializer<GroupPost.ContentType> {
    
    @Override
    public GroupPost.ContentType deserialize(JsonParser p, DeserializationContext ctxt) 
            throws IOException, JsonProcessingException {
        String value = p.getValueAsString();
        if (value == null) {
            return null;
        }
        try {
            return GroupPost.ContentType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid content type: " + value + 
                ". Valid values are: TEXT, IMAGE, FILE, MIXED, MARKDOWN");
        }
    }
}
