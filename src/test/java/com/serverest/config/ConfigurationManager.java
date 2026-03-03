package com.serverest.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    private static Properties properties;

    static {
        try {
            properties = new Properties();
            InputStream inputStream = ConfigurationManager.class.getClassLoader()
                .getResourceAsStream("config/config.properties");

            if (inputStream == null) {
                throw new RuntimeException("Arquivo config/config.properties não encontrado em src/test/resources");
            }

            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar o arquivo de configuração", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}