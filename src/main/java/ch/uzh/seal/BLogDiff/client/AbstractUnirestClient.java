package ch.uzh.seal.BLogDiff.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;

import java.io.IOException;

public abstract class AbstractUnirestClient {

    /**
     * Sets a custom ObjectMapper
     */
    protected void setupUnirest() {
        Unirest.config().setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
