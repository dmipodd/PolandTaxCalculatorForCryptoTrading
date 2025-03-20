package com.dpod.crypto.taxcalc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.experimental.UtilityClass;

import java.io.IOException;

import static com.dpod.crypto.taxcalc.util.FileUtils.opeInputStreamFor;

@UtilityClass
public class ConfigLoader {

    public static AppConfig loadConfig(String file) throws IOException {
        var yamlFactory = new YAMLFactory();
        var objectMapper = new ObjectMapper(yamlFactory);
        try (var inputStream = opeInputStreamFor(file)) {
            return objectMapper.readValue(inputStream, AppConfig.class);
        }
    }
}