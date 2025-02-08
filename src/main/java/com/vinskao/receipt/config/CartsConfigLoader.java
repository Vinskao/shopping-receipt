package com.vinskao.receipt.config;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinskao.receipt.model.CartDO;

/**
 * CartsConfigLoader 類別負責載入購物車設定檔案，並將 JSON 資料轉換為 CartDO 物件。
 * @author VinsKao
 */
public class CartsConfigLoader {
    private static final String CONFIG_FILE = "carts.json";

    public static CartDO load() {
        // 創建 Jackson 的 ObjectMapper 實例，用於 JSON 轉換
        ObjectMapper mapper = new ObjectMapper();
        // 使用 try-with-resources 自動關閉資源
        try (InputStream in = LocationsConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new RuntimeException("找不到配置檔 " + CONFIG_FILE);
            }
            // 將 JSON 內容轉換為 CartDO 物件並返回
            return mapper.readValue(in, CartDO.class);
        } catch (Exception e) {
            throw new RuntimeException("載入配置失敗", e);
        }
    }
} 