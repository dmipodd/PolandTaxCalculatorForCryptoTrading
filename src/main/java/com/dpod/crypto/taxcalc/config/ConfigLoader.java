package com.dpod.crypto.taxcalc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;

import static com.dpod.crypto.taxcalc.util.FileUtils.opeInputStreamFor;

@UtilityClass
public class ConfigLoader {

    public static AppConfig loadConfig(String file) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (InputStream inputStream = opeInputStreamFor(file)) {
            return mapper.readValue(inputStream, AppConfig.class);
        }
    }
}