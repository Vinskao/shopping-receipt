package com.vinskao.receipt.config;

// 導入用於 JSON 處理的 ObjectMapper 類別
import com.fasterxml.jackson.databind.ObjectMapper;
// 導入地區數據對象類別
import com.vinskao.receipt.model.LocationDO;
// 導入用於文件讀取的輸入流類別
import java.io.InputStream;

/**
 * LocationsConfigLoader 類別負責載入地區設定檔案，並將 JSON 資料轉換為 LocationDO 物件。
 * @author VinsKao
 */
public class LocationsConfigLoader {
    private static final String CONFIG_FILE = "locations.json";

    public static LocationDO load() {
        // 創建 Jackson 的 ObjectMapper 實例，用於 JSON 轉換
        ObjectMapper mapper = new ObjectMapper();
        // 使用 try-with-resources 自動關閉資源，從類路徑中讀取地區配置文件
        try (InputStream in = LocationsConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            // 如果找不到配置文件，拋出運行時異常
            if (in == null) {
                throw new RuntimeException("找不到配置檔 " + CONFIG_FILE);
            }
            // 將 JSON 內容轉換為 LocationDO 物件並返回
            return mapper.readValue(in, LocationDO.class);
        } catch (Exception e) {
            throw new RuntimeException("載入配置失敗", e);
        }
    }
} 