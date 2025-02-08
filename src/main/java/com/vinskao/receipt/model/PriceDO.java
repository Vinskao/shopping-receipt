package com.vinskao.receipt.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 * PriceDO 表示商品價格資訊的資料物件，用來封裝從 prices.json 中讀入的資料。
 * 使用一個 Map 儲存商品名稱 (key) 與對應價格 (value)。
 * @author VinsKao
 */
public class PriceDO {
    // 用來儲存商品價格資訊，key 為商品名稱，value 為對應的價格
    private Map<String, BigDecimal> prices = new HashMap<>();

    public PriceDO() {
    }

    public Map<String, BigDecimal> getPrices() {
        return prices; 
    }

    /**
     * 處理 JSON 中不固定的屬性（沒有事先定義好成員變數），
     * 當讀取 JSON 時，遇到新增的屬性會呼叫此方法進行設定。
     *
     * @param key   商品名稱，作為價格資料的key值
     * @param value 該商品對應的價格 (BigDecimal)
     */
    @JsonAnySetter
    public void addPrice(String key, BigDecimal value) {
        this.prices.put(key, value); // 把傳入的商品價格資料放入 prices Map 中
    }
}
