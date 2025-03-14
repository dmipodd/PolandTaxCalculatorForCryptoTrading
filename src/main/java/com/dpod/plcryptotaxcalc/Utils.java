package com.dpod.plcryptotaxcalc;

import lombok.experimental.UtilityClass;

import java.io.InputStream;

@UtilityClass
public class Utils {

    public static InputStream openFile(String filename) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(filename);
    }
}
