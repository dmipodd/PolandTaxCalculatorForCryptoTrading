package com.dpod.crypto.taxcalc.util;

import com.dpod.crypto.taxcalc.config.AppConfig;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@UtilityClass
public class FileUtils {

    public static InputStream opeInputStreamFor(String fileInClassPath) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(fileInClassPath);
    }

    public static String generateOutputFileName(AppConfig config) {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return String.format("%s.result.%d.%s.csv",
                config.source().name().toLowerCase(),
                config.year(),
                currentDateTime);
    }

    public static void writeRowsToCsv(List<String> rows, String outputFilename) throws IOException {
        Path outputFilePath = Path.of(outputFilename);
        Files.write(outputFilePath, rows, StandardOpenOption.CREATE_NEW);
        log.info("Tax report is saved to {}", outputFilePath.toFile().getAbsoluteFile());
    }
}