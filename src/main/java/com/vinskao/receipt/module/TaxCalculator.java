package com.vinskao.receipt.module;

import java.math.BigDecimal;
import java.util.List;

import com.vinskao.receipt.config.LocationsConfigLoader;
import com.vinskao.receipt.model.ItemVO;
import com.vinskao.receipt.model.LocationDO;

/**
 * TaxCalculator 類別負責根據商品的所在區域與類別資訊計算所需繳納的稅金。
 * 此類別在建構時會從設定檔中讀取各地區的稅率與免稅產品類別配置，
 * 並透過 {@link #determineTax(ItemVO)} 方法根據商品資訊判斷是否需課稅，
 * 若屬於免稅產品，則回傳零稅率，否則回傳該區域之標準稅率。
 * 
 * @author VinsKao
 */
public class TaxCalculator {

    /**
     * 儲存各地區之稅率與免稅類別設定。
     * 此設定物件由 {@link LocationsConfigLoader} 讀取數據，
     * 用以判斷各地區適用的稅率與免稅產品類別。
     */
    private LocationDO locationsConfig;
    
    /**
     * 建構子 TaxCalculator
     * 物件建立時自動從locations.json中載入各地區稅設定，
     * 並將配置資訊儲存於 {@code locationsConfig} 成員變數之中，
     * 以便後續計算稅金時使用。
     */
    public TaxCalculator(){
        this.locationsConfig = LocationsConfigLoader.load();
    }
    
    /**
     * 根據商品資訊決定並回傳應用的稅率。
     *
     * @param item 傳入欲計算稅金的商品物件，必須包含正確的 {@code Location} 與產品名稱資訊
     * @return 若商品符合免稅條件則回傳 {@link BigDecimal#ZERO}，否則回傳該區域的稅率
     * @throws IllegalArgumentException 當 {@code item} 或其 {@code Location} 為 null 時拋出此異常
     */
    public BigDecimal determineTax(ItemVO item){
        if(item == null || item.getLocation() == null){
            throw new IllegalArgumentException("Item 或 Location 不能為 null");
        }
        
        // 取得商品所在區域的名稱
        String location = item.getLocation().name();
        // 取得商品之產品名稱（用於比對免稅條件）
        String category = item.getProductName();

        // 從配置中取得該地區的稅率
        BigDecimal taxRate = locationsConfig.getTaxRate(location);
        // 從配置中取得該地區的免稅產品類別列表
        List<String> exemptCategories = locationsConfig.getExemptCategories(location);
        
        // 若商品的產品名稱包含於免稅類別中，則回傳0
        if(exemptCategories.contains(category)){
            return BigDecimal.ZERO;
        }
        // 回傳該區域的稅
        return taxRate;        
    }

    /**
     * 計算商品列表中所有商品稅金的總和。
     *
     * @param items 需要計算稅金的商品列表。列表中不可包含 null 值。
     * @return 所有商品稅金的加總。
     * @throws IllegalArgumentException 當 items 為 null 時或任何商品為 null 時拋出此異常。
     */
    public BigDecimal calculateTotalTax(List<ItemVO> items) {
        if (items == null) {
            throw new IllegalArgumentException("商品列表不能為 null");
        }
        
        // 將商品列表轉換成 Stream
        return items.stream()
                    .map(item -> {
                        // 根據當前商品決定適用的稅（如果商品免稅，會是 0）
                        BigDecimal taxRate = determineTax(item);
                        // 計算該商品總價
                        BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                        // 計算該商品需支付的稅額（稅額 = 總價 * 稅率）
                        return itemTotal.multiply(taxRate);
                    })
                    // 將所有計算出來的稅額相加， 從 0 起始累加每一次map中的return
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
