package com.vinskao.receipt.module;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.vinskao.receipt.model.ItemVO;
import com.vinskao.receipt.model.LocationDO;
import com.vinskao.receipt.model.LocationENUM;
import com.vinskao.receipt.model.PriceDO;

/**
 * TaxCalculator 的單元測試類
 * 測試稅率計算相關的功能，包括：
 * 1. 一般商品的稅率計算
 * 2. 免稅商品的稅率判定
 * 3. 購物車內多項商品的總稅額計算
 * 
 * @author VinsKao
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TaxCalculatorTest {
    
    @Mock
    private LocationDO locationsConfig;  
    
    @Mock
    private PriceDO pricesConfig; 
    
    @InjectMocks
    private TaxCalculator taxCalculator;

    @BeforeEach
    public void setup() {
        // MockitoExtension 會自動初始化 @Mock 與 @InjectMocks
    }

    /**
     * 測試需要徵稅的商品稅率計算
     */
    @Test
    public void testDetermineTax_WithTax() {
        // 模擬 CA 一般商品，稅率 9.75%
        LocationENUM locationCode = LocationENUM.CA;
        String productName = "laptop";
        BigDecimal expectedTax = new BigDecimal("0.0975");

        // 模擬返回的稅率配置
        when(locationsConfig.getTaxRate(locationCode.name())).thenReturn(expectedTax);
        // 模擬該地區沒有免稅類別
        when(locationsConfig.getExemptCategories(locationCode.name())).thenReturn(Collections.emptyList());
        
        // 準備一個需要徵稅的商品資料
        ItemVO item = new ItemVO();
        item.setLocation(locationCode);
        item.setCategory("electronics");
        item.setProductName(productName);

        // 執行測試並驗證結果
        BigDecimal tax = taxCalculator.determineTax(item);
        assertEquals(expectedTax, tax);
    }

    /**
     * 測試免稅商品的稅率計算
     */
    @Test
    public void testDetermineTax_WithoutTax() {
        // 模擬加州的免稅食品商品
        LocationENUM locationCode = LocationENUM.CA;
        String productName = "apple";
        BigDecimal expectedTax = BigDecimal.ZERO;

        // 模擬該地區的稅率配置
        when(locationsConfig.getTaxRate(locationCode.name())).thenReturn(new BigDecimal("0.0975"));
        // 模擬食品類別為免稅類別
        when(locationsConfig.getExemptCategories(locationCode.name())).thenReturn(Arrays.asList("food"));

        // 準備一個免稅商品資料
        ItemVO item = new ItemVO();
        item.setLocation(locationCode);
        item.setCategory("food");
        item.setProductName(productName);

        // 執行測試並驗證結果
        BigDecimal tax = taxCalculator.determineTax(item);
        assertEquals(expectedTax, tax);
    }

    /**
     * 測試購物車內多項商品的總稅額計算
     */
    @Test
    public void testCalculateTotalTax() {
        // 設置測試基本資料
        LocationENUM locationCode = LocationENUM.CA;
        String product1 = "laptop";
        String product2 = "apple";
        
        // 設置預期稅額
        BigDecimal expectedTax1 = new BigDecimal("9.75");  // 筆電稅（100 * 0.0975）
        BigDecimal expectedTax2 = BigDecimal.ZERO;         // 免稅

        // 模擬地區稅率配置
        when(locationsConfig.getTaxRate(locationCode.name())).thenReturn(new BigDecimal("0.0975"));
        // 模擬食品類別為免稅
        when(locationsConfig.getExemptCategories(locationCode.name())).thenReturn(Arrays.asList("food"));

        // 創建並模擬商品價格配置
        Map<String, BigDecimal> priceMap = new HashMap<>();
        priceMap.put(product1, new BigDecimal("100"));  // 筆電價格
        priceMap.put(product2, new BigDecimal("1"));    // 蘋果價格
        when(pricesConfig.getPrices()).thenReturn(priceMap);

        // 準備第一個商品：筆電
        ItemVO item1 = new ItemVO();
        item1.setLocation(locationCode);
        item1.setCategory("electronics");
        item1.setProductName(product1);
        item1.setPrice(new BigDecimal("100"));
        item1.setQuantity(1);

        // 準備第二個商品：蘋果
        ItemVO item2 = new ItemVO();
        item2.setLocation(locationCode);
        item2.setCategory("food");
        item2.setProductName(product2);
        item2.setPrice(new BigDecimal("1"));
        item2.setQuantity(2);

        // 將商品加入測試清單
        Collection<ItemVO> items = Arrays.asList(item1, item2);

        // 執行測試並驗證結果（只比較到小數點後兩位）
        BigDecimal totalTax = taxCalculator.calculateTotalTax(items);
        BigDecimal expectedTotalTax = expectedTax1.add(expectedTax2);
        assertEquals(expectedTotalTax.setScale(2, BigDecimal.ROUND_HALF_UP), 
                     totalTax.setScale(2, BigDecimal.ROUND_HALF_UP));
    }
} 