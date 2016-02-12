package io.einhard.servicemanager.services;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ConfigServiceTest {
    @Test
    public void simpleConfigServiceTest() {
        JsonObject config = Json.parse("{\"somekey\": \"somevalue\"}").asObject();

        ConfigService configService = new ConfigService(null, config);

        assertEquals("somevalue", configService.getConfig().get("somekey").asString());
    }

    @Test
    public void getInConfigTest() {
        JsonObject config = Json.parse("{\"one\": {\"two\": {\"three\": 4}}}").asObject();

        ConfigService configService = new ConfigService(null, config);

        assertEquals(4, configService.getInConfig("one", "two", "three").asInt());
    }
}
