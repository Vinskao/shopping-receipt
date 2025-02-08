package com.vinskao.receipt.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public enum LocationENUM {
    CA,
    NY, 
    NA;

    // 外部注入 LocationsDO 中的 locations 設定
    private static Map<String, Map<String, Object>> locationConfigs;

    /**
     * 注入配置，必須於應用初始化時呼叫一次。
     * 注入內容建議由 LocationsConfigLoader.load() 取得後，調用 getLocations() 取得。
     */
    public static void setLocationConfigs(Map<String, Map<String, Object>> configs) {
        locationConfigs = configs;
    }

    private Map<String, Object> getConfig() {
        if (locationConfigs == null) {
            throw new IllegalStateException("Location configs 尚未初始化");
        }
        Map<String, Object> config = locationConfigs.get(this.name());
        if (config == null) {
            throw new IllegalArgumentException("未知地區: " + this.name());
        }
        return config;
    }

    public BigDecimal getTaxRate() {
        Object taxRateObj = getConfig().get("taxRate");
        if (taxRateObj == null) {
            throw new IllegalArgumentException("taxRate 未定義: " + this.name());
        }
        return new BigDecimal(taxRateObj.toString());
    }

    @SuppressWarnings("unchecked")
    public List<String> getExemptCategories() {
        Object list = getConfig().get("exemptCategories");
        if (list == null) {
            throw new IllegalArgumentException("exemptCategories 未定義: " + this.name());
        }
        return (List<String>) list;
    }
}