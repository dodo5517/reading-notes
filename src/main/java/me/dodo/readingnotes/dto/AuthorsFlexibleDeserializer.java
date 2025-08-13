package me.dodo.readingnotes.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuthorsFlexibleDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<String> result = new ArrayList<>();

        JsonToken token = p.currentToken();
        if (token == JsonToken.START_ARRAY) {
            // ["김누구","이누구"] 형태
            while (p.nextToken() != JsonToken.END_ARRAY) {
                result.add(p.getValueAsString());
            }
            return result;
        }

        // 단일 문자열도 허용
        result.add(p.getValueAsString());
        return result;
    }
}