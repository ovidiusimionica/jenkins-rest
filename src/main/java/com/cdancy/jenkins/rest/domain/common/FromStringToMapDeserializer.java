package com.cdancy.jenkins.rest.domain.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FromStringToMapDeserializer extends JsonDeserializer<Map>
{
    @Override
    public Map deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        String value = p.getText();
        if (value == null || value.trim().isEmpty())
        {
            return null;
        } else
        {
            Map<String, String> parameters = new HashMap<>();
            value = value.trim();
            for (String keyValue : value.split("\n"))
            {
                String[] pair = keyValue.split("=");
                parameters.put(pair[0], pair.length > 1 ? pair[1] : "");
            }
            return parameters;
        }
    }
}
