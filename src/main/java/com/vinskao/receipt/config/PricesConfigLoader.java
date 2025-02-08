package com.vinskao.receipt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinskao.receipt.model.PriceDO;

import java.io.InputStream;

public class PricesConfigLoader {
    private static final String CONFIG_FILE = "prices.json";

    public static PriceDO load() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = LocationsConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new RuntimeException("找不到配置檔 " + CONFIG_FILE);
            }
            return mapper.readValue(in, PriceDO.class);
        } catch (Exception e) {
            throw new RuntimeException("載入配置失敗", e);
        }
    }
}