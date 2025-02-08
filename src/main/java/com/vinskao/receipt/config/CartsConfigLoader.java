package com.vinskao.receipt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinskao.receipt.model.LocationDO;

import java.io.InputStream;

public class CartsConfigLoader {
    private static final String CONFIG_FILE = "carts.json";

    public static LocationDO load() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = LocationsConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new RuntimeException("找不到配置檔 " + CONFIG_FILE);
            }
            return mapper.readValue(in, LocationDO.class);
        } catch (Exception e) {
            throw new RuntimeException("載入配置失敗", e);
        }
    }
} 