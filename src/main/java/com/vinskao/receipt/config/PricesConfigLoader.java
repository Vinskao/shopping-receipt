package com.vinskao.receipt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinskao.receipt.model.PriceDO;

import java.io.InputStream;

/**
 * PricesConfigLoader 類別負責載入價格設定檔案，並將 JSON 資料轉換為 PriceDO 物件。
 * @author VinsKao
 */
public class PricesConfigLoader {
    private static final String CONFIG_FILE = "prices.json";

    public static PriceDO load() {
        // 創建 Jackson 的 ObjectMapper 實例，用於 JSON 轉換
        ObjectMapper mapper = new ObjectMapper();
        // 使用 try-with-resources 自動關閉資源，從類路徑中讀取價格配置文件
        try (InputStream in = LocationsConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new RuntimeException("找不到配置檔 " + CONFIG_FILE);
            }
            // 將 JSON 內容轉換為 PriceDO 物件並返回
            return mapper.readValue(in, PriceDO.class);
        } catch (Exception e) {
            throw new RuntimeException("載入配置失敗", e);
        }
    }
}