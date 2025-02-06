package com.vinskao.receipt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

public class PropertiesConfigLoader {
    private static final String CONFIG_FILE = "properties.json";

    public static PropertiesConfig load() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = PropertiesConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new RuntimeException("找不到配置檔 " + CONFIG_FILE);
            }
            return mapper.readValue(in, PropertiesConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("載入配置失敗", e);
        }
    }
} 