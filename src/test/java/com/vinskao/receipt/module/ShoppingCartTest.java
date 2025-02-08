package com.vinskao.receipt.module;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.vinskao.receipt.model.ItemVO;
import com.vinskao.receipt.model.PriceDO;

/**
 * ShoppingCart 的單元測試類
 * 測試購物車相關的功能，包括：
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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * 測試購物車小計計算
     * 場景：計算多個商品的價格總和（不含稅）
     */
    @Test
    public void testCalSubtotal() {
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

    /**
     * 測試購物車總稅金計算
     * 場景：計算多個商品的稅金總和
     */
    @Test
    public void testCalTax() {
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

    /**
     * 測試購物車總金額計算
     * 場景：計算含稅總金額
     */
    @Test
    public void testCalTotal() {
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

    /**
     * 測試商品名稱格式化
     * 場景：測試各種格式的商品名稱轉換
     */
    @Test
    public void testItemNameFormatter() {
        // 測試底線轉換為空格，並將首字母大寫
        assertEquals("Laptop Computer", shoppingCart.itemNameFormatter("laptop_computer"));
        
        // 測試全大寫轉換
        assertEquals("Laptop", shoppingCart.itemNameFormatter("LAPTOP"));
        
        // 測試多個底線
        assertEquals("This Is A Test", shoppingCart.itemNameFormatter("this_is_a_test"));
        
        // 測試空字串
        assertEquals("", shoppingCart.itemNameFormatter(""));
        
        // 測試 null 值
        assertNull(shoppingCart.itemNameFormatter(null));
    }
} 