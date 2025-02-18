package com.vinskao.receipt.module;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
 * TaxCalculator 單元測試
 * 測試稅率計算，包括：
 * 1. 一般商品的稅率計算
 * 2. 免稅商品的稅率判定
 * 3. 購物車內多項商品的總稅額計算
 * 
 * @author VinsKao
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaxCalculatorTest {
    
    @Mock
    private LocationDO locationsConfig;  
    
    @Mock
    private PriceDO pricesConfig; 
    
    @InjectMocks
    private TaxCalculator taxCalculator;

    /**
     * 測試前的初始化設置
     * 設定CA的稅率為 9.75% 和免稅類別為食品類
     */
    @BeforeEach
    void setup() {
        when(locationsConfig.getTaxRate(LocationENUM.CA.name())).thenReturn(new BigDecimal("0.0975"));
        when(locationsConfig.getExemptCategories(LocationENUM.CA.name())).thenReturn(List.of("food"));
    }

    @Nested
    class DetermineTaxTests {
        /**
         * 測試需要課稅商品的稅率計算
         * 驗證非免稅商品是否正確計算稅率
         */
        @Test
        void shouldCalculateTaxForTaxableItem() {
            // Given
            ItemVO item = createItemVO(LocationENUM.CA, "electronics", "laptop");

            // When
            BigDecimal tax = taxCalculator.determineTax(item);

            // Then
            assertEquals(new BigDecimal("0.0975"), tax);
        }

        /**
         * 測試免稅商品的稅率計算
         * 驗證免稅商品是否正確返回0稅率
         */
        @Test
        void shouldReturnZeroTaxForExemptItem() {
            // Given
            ItemVO item = createItemVO(LocationENUM.CA, "food", "apple");

            // When
            BigDecimal tax = taxCalculator.determineTax(item);

            // Then
            assertEquals(BigDecimal.ZERO, tax);
        }

        /**
         * 測試傳入空值商品時的異常處理
         * 驗證是否拋出適當的異常信息
         */
        @Test
        void shouldThrowExceptionForNullItem() {
            assertThrows(IllegalArgumentException.class, 
                () -> taxCalculator.determineTax(null),
                "Item 或 Location 不能為 null");
        }

        /**
         * 測試商品缺少位置信息時的異常處理
         * 驗證是否拋出適當的異常信息
         */
        @Test
        void shouldThrowExceptionForItemWithNullLocation() {
            // Given
            ItemVO item = new ItemVO();
            item.setProductName("laptop");

            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> taxCalculator.determineTax(item),
                "Item 或 Location 不能為 null");
        }
    }

    @Nested
    class CalculateTotalTaxTests {
        /**
         * 測試多個商品的總稅額計算
         * 驗證包含應稅和免稅商品的購物車總稅額是否正確
         */
        @Test
        void shouldCalculateTotalTaxForMultipleItems() {
            // Given
            setupPriceMap();
            Collection<ItemVO> items = List.of(
                createFullItemVO(LocationENUM.CA, "electronics", "laptop", "100", 1),
                createFullItemVO(LocationENUM.CA, "food", "apple", "1", 2)
            );

            // When
            BigDecimal totalTax = taxCalculator.calculateTotalTax(items);

            // Then
            assertEquals(new BigDecimal("9.75"), totalTax.setScale(2, RoundingMode.HALF_UP));
        }

        /**
         * 測試傳入空值商品列表時的異常處理
         * 驗證是否拋出適當的異常信息
         */
        @Test
        void shouldThrowExceptionForNullItemsList() {
            assertThrows(IllegalArgumentException.class, 
                () -> taxCalculator.calculateTotalTax(null),
                "商品列表不能為 null");
        }

        /**
         * 測試商品列表中包含空值項目時的異常處理
         * 驗證是否拋出適當的異常信息
         */
        @Test
        void shouldThrowExceptionForListContainingNullItem() {
            // Given
            Collection<ItemVO> items = Arrays.asList(createItemVO(LocationENUM.CA, "electronics", "laptop"), null);

            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> taxCalculator.calculateTotalTax(items),
                "商品列表中包含 null 項目");
        }
    }

    /**
     * 創建基本商品對象的輔助方法
     * 
     * @param location 商品所在地區
     * @param category 商品類別
     * @param productName 商品名稱
     * @return 創建的商品對象
     */
    private ItemVO createItemVO(LocationENUM location, String category, String productName) {
        ItemVO item = new ItemVO();
        item.setLocation(location);
        item.setCategory(category);
        item.setProductName(productName);
        return item;
    }

    /**
     * 創建完整商品對象的輔助方法
     * 
     * @param location 商品所在地區
     * @param category 商品類別
     * @param productName 商品名稱
     * @param price 商品價格
     * @param quantity 商品數量
     * @return 創建的完整商品對象
     */
    private ItemVO createFullItemVO(LocationENUM location, String category, 
            String productName, String price, int quantity) {
        ItemVO item = createItemVO(location, category, productName);
        item.setPrice(new BigDecimal(price));
        item.setQuantity(quantity);
        return item;
    }

    /**
     * 設置商品價格映射的輔助方法
     * 用於模擬價格配置
     */
    private void setupPriceMap() {
        Map<String, BigDecimal> priceMap = new HashMap<>();
        priceMap.put("laptop", new BigDecimal("100"));
        priceMap.put("apple", new BigDecimal("1"));
        when(pricesConfig.getPrices()).thenReturn(priceMap);
    }
} 