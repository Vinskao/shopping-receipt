package com.vinskao.receipt.config;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 此類直接對應 properties.json 中的配置，
 * JSON 結構為：
 * {
 *   "locations": {
 *     "CA": {
 *       "taxRate": 0.0975,
 *       "exemptCategories": ["food"]
 *     },
 *     "NY": {
 *       "taxRate": 0.08875,
 *       "exemptCategories": ["food", "clothing"]
 *     }
 *   }
 * }
 */
public class PropertiesConfig {

    // locations 的 value 為一個 Map，包含 "taxRate" 與 "exemptCategories" 兩個鍵
    private Map<String, Map<String, Object>> locations;

    public Map<String, Map<String, Object>> getLocations() {
        return locations;
    }

    public void setLocations(Map<String, Map<String, Object>> locations) {
        this.locations = locations;
    }

    /**
     * 取得指定地區的稅率。
     * @param location 地區代號，例如 "CA" 或 "NY"
     * @return 稅率 (BigDecimal)
     */
    public BigDecimal getTaxRate(String location) {
        if (locations == null || !locations.containsKey(location)) {
            throw new IllegalArgumentException("未知地區: " + location);
        }
        Object taxRateObj = locations.get(location).get("taxRate");
        if (taxRateObj == null) {
            throw new IllegalArgumentException("taxRate 未定義: " + location);
        }
        return new BigDecimal(taxRateObj.toString());
    }

    /**
     * 取得指定地區的免稅商品類別清單。
     * @param location 地區代號，例如 "CA" 或 "NY"
     * @return 免稅類別清單
     */
    @SuppressWarnings("unchecked")
    public List<String> getExemptCategories(String location) {
        if (locations == null || !locations.containsKey(location)) {
            throw new IllegalArgumentException("未知地區: " + location);
        }
        Object list = locations.get(location).get("exemptCategories");
        if (list == null) {
            throw new IllegalArgumentException("exemptCategories 未定義: " + location);
        }
        return (List<String>) list;
    }
} 