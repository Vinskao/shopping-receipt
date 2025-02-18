package com.vinskao.receipt.module;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.vinskao.receipt.model.ItemVO;
import com.vinskao.receipt.model.PriceDO;

/**
 * ShoppingCart 單元測試
 * 測試購物車，包括：
 * 1. 小計金額計算
 * 2. 稅金計算
 * 3. 總金額計算
 * 4. 商品名稱格式化
 * @author VinsKao
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ShoppingCartTest {
    
    @Mock
    private PriceDO priceDO;  
    
    @Mock
    private TaxCalculator taxCalculator; 
    
    @InjectMocks
    private ShoppingCart shoppingCart;

    /**
     * 小計計算測試類
     * 包含購物車商品小計金額的計算測試
     */
    @Nested
    class SubtotalCalculationTests {
        /**
         * 測試購物車小計計算
         * 場景：計算多個商品的價格總和（不含稅）
         * 測試步驟：
         * 1. 建立測試商品資料（筆電和蘋果）
         * 2. 計算商品總價
         * 3. 驗證計算結果是否正確
         */
        @Test
        public void shouldCalculateCorrectSubtotal() {
            // 準備測試數據
            String product1 = "laptop";
            String product2 = "apple";

            // 準備第一個商品：筆電
            ItemVO item1 = new ItemVO();
            item1.setProductName(product1);
            item1.setPrice(new BigDecimal("100"));
            item1.setQuantity(1);

            // 準備第二個商品：蘋果
            ItemVO item2 = new ItemVO();
            item2.setProductName(product2);
            item2.setPrice(new BigDecimal("1"));
            item2.setQuantity(2);

            // 將商品加入測試清單
            Collection<ItemVO> items = Arrays.asList(item1, item2);

            // 執行測試
            BigDecimal subtotal = shoppingCart.calSubtotal(items);
            
            // 驗證結果：100 * 1 + 1 * 2 = 102
            assertEquals(new BigDecimal("102"), subtotal);
        }
    }

    /**
     * 稅金計算測試類
     * 包含購物車商品稅金的計算測試
     */
    @Nested
    class TaxCalculationTests {
        /**
         * 測試購物車稅金計算
         * 場景：計算多個商品的稅金總和
         * 測試步驟：
         * 1. 準備測試商品清單
         * 2. 模擬稅金計算器行為
         * 3. 驗證計算結果是否正確
         */
        @Test
        public void shouldCalculateCorrectTax() {
            // 準備測試數據
            Collection<ItemVO> items = Arrays.asList(new ItemVO());
            BigDecimal expectedTax = new BigDecimal("9.75");
            
            // 模擬 TaxCalculator 的行為
            when(taxCalculator.calculateTotalTax(items)).thenReturn(expectedTax);

            // 執行測試
            BigDecimal tax = shoppingCart.calTax(items);
            
            // 驗證結果
            assertEquals(expectedTax, tax);
        }
    }

    /**
     * 總金額計算測試類
     * 包含購物車商品總金額（含稅）的計算測試
     */
    @Nested
    class TotalCalculationTests {
        /**
         * 測試購物車總金額計算
         * 場景：計算含稅總金額
         * 測試步驟：
         * 1. 準備測試數據（小計和稅金）
         * 2. 使用 spy 物件模擬方法
         * 3. 驗證總金額計算結果是否正確
         */
        @Test
        public void shouldCalculateCorrectTotal() {
            // 準備測試數據
            Collection<ItemVO> items = Arrays.asList(new ItemVO());
            BigDecimal subtotal = new BigDecimal("100");
            BigDecimal tax = new BigDecimal("9.75");
            
            // 創建一個 spy 物件來模擬部分方法
            ShoppingCart spyCart = spy(shoppingCart);
            
            // 模擬方法行為
            doReturn(subtotal).when(spyCart).calSubtotal(items);
            doReturn(tax).when(spyCart).calTax(items);

            // 執行測試
            BigDecimal total = spyCart.calTotal(items);
            
            // 驗證結果：100 + 9.75 = 109.75
            assertEquals(new BigDecimal("109.75"), total);
        }
    }

    /**
     * 商品名稱格式化測試類
     * 商品名稱格式轉換測試案例
     */
    @Nested
    class ItemNameFormattingTests {
        /**
         * 測試商品名稱格式化功能
         * 場景：測試商品名稱轉換
         * 測試案例：
         * 1. 底線轉換為空格
         * 2. 全大寫轉換
         * 3. 多個底線的轉換
         * 4. 空字串處理
         * 5. null 值處理
         */
        @Test
        public void shouldFormatItemNamesCorrectly() {
            // 測試底線轉換為空格，並將首字母大寫
            assertEquals("Laptop Computer", shoppingCart.itemNameFormatter("laptop_computer"));
            
            // 測試全大寫轉換
            assertEquals("Laptop", shoppingCart.itemNameFormatter("LAPTOP"));
            
            // 測試多個底線
            assertEquals("I Am Vinskao", shoppingCart.itemNameFormatter("i_am_vinskao"));
            
            // 測試空字串
            assertEquals("", shoppingCart.itemNameFormatter(""));
            
            // 測試 null 值
            assertNull(shoppingCart.itemNameFormatter(null));
        }
    }
} 