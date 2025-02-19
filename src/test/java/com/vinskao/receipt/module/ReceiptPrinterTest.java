package com.vinskao.receipt.module;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.HashMap;
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
import com.vinskao.receipt.model.LocationENUM;

/**
 * ReceiptPrinter 單元測試
 * 
 * @author VinsKao
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ReceiptPrinterTest {
    
    @Mock
    private ShoppingCart shoppingCart;
    
    @InjectMocks
    private ReceiptPrinter receiptPrinter;

    @BeforeEach
    void setup() {
        // 移除固定的模擬設置，改為在每個測試案例中設置特定的回傳值
    }

    /**
     * 商品名稱格式化功能的測試集合
     */
    @Nested
    class ItemNameFormatterTests {
        
        /**
         * 測試商品名稱的標準格式化。
         * 
         * 測試案例：
         * 1. 底線轉換為空格
         * 2. 單字首字母大寫
         * 3. 全大寫字串轉換
         * 4. 多重底線處理
         */
        @Test
        void shouldFormatItemNamesCorrectly() {
            assertEquals("Laptop Computer", receiptPrinter.itemNameFormatter("laptop_computer"),
                    "應將底線轉換為空格並將每個單字首字母大寫");
            
            assertEquals("Laptop", receiptPrinter.itemNameFormatter("LAPTOP"),
                    "應將全大寫字串轉換為首字母大寫格式");
            
            assertEquals("I Am Vinskao", receiptPrinter.itemNameFormatter("i_am_vinskao"),
                    "應正確處理多個底線的情況");
        }

        /**
         * 測試商品名稱格式化的邊界。
         * 
         * 測試案例：
         * 1. 空字串輸入
         * 2. null 值輸入
         */
        @Test
        void shouldHandleSpecialCases() {
            assertEquals("", receiptPrinter.itemNameFormatter(""),
                    "空字串應該回傳空字串");
            
            assertNull(receiptPrinter.itemNameFormatter(null),
                    "null 輸入應該回傳 null");
        }
    }

    /**
     * 收據表格生成相關的測試類別。
     * 測試收據的整體格式、計算和呈現。
     */
    @Nested
    class ReceiptFrameTests {
        
        /**
         * 測試收據表格生成的基本功能。
         * 驗證表格生成過程不會拋出異常。
         */
        @Test
        void shouldPrintReceiptFramesWithoutException() {
            assertDoesNotThrow(() -> receiptPrinter.printReceiptFrames(),
                    "收據表格生成不應拋出異常");
        }

        /**
         * 測試收據列印 - 案例1。
         * 
         * 測試場景：
         * - 購物項目：book(1)和chip(1)
         * - 驗證小計、稅額和總金額的計算
         * - 驗證收據格式的正確性
         */
        @Test
        void shouldPrintReceiptCase1() {
            // 準備測試資料
            Map<String, ItemVO> items = new HashMap<>();
            ItemVO book = new ItemVO();
            book.setProductName("book");
            book.setQuantity(1);
            
            ItemVO chips = new ItemVO();
            chips.setProductName("potato_chips");
            chips.setQuantity(1);
            
            items.put("item1", book);
            items.put("item2", chips);

            Map<String, BigDecimal> prices = new HashMap<>();
            prices.put("book", new BigDecimal("17.99"));
            prices.put("potato_chips", new BigDecimal("3.99"));

            // 設置模擬行為
            when(shoppingCart.calSubtotal(anyCollection())).thenReturn(new BigDecimal("21.98"));
            when(shoppingCart.calTax(anyCollection())).thenReturn(new BigDecimal("1.80"));
            when(shoppingCart.calTotal(anyCollection())).thenReturn(new BigDecimal("23.78"));

            // 執行測試
            String receipt = receiptPrinter.getTable(items, prices);

            // 驗證輸出
            String expectedReceipt = 
                "+-----------------------------------------------------+\n" +
                "|item                          price               qty|\n" +
                "|                                                     |\n" +
                "|Potato Chips                   3.99                 1|\n" +
                "|Book                          17.99                 1|\n" +
                "|                                                     |\n" +
                "|subtotal                                        21.98|\n" +
                "|tax                                              1.80|\n" +
                "|total                                           23.78|\n" +
                "+-----------------------------------------------------+";
            assertEquals(expectedReceipt, receipt);
        }

        /**
         * 測試收據列印 - 案例2。
         * 
         * 測試場景：
         * - 購物項目：book(1)和pencil(3)
         * - 所有商品來自NY
         * - 驗證小計、稅額和總金額的計算
         * - 驗證收據格式的正確性
         */
        @Test
        void shouldPrintReceiptCase2() {
            // 準備測試資料
            Map<String, ItemVO> items = new HashMap<>();
            ItemVO book = new ItemVO();
            book.setProductName("book");
            book.setQuantity(1);
            book.setLocation(LocationENUM.NY);
            book.setCategory("misc");
            
            ItemVO pencil = new ItemVO();
            pencil.setProductName("pencil");
            pencil.setQuantity(3);
            pencil.setLocation(LocationENUM.NY);
            pencil.setCategory("misc");
            
            items.put("item1", book);
            items.put("item2", pencil);

            Map<String, BigDecimal> prices = new HashMap<>();
            prices.put("pencil", new BigDecimal("2.99"));
            prices.put("book", new BigDecimal("17.99"));

            // 為這個特定測試案例設置模擬行為
            when(shoppingCart.calSubtotal(anyCollection())).thenReturn(new BigDecimal("26.96"));
            when(shoppingCart.calTax(anyCollection())).thenReturn(new BigDecimal("2.40"));
            when(shoppingCart.calTotal(anyCollection())).thenReturn(new BigDecimal("29.35"));

            // 執行測試
            String receipt = receiptPrinter.getTable(items, prices);

            // 驗證輸出
            String expectedReceipt = 
                "+-----------------------------------------------------+\n" +
                "|item                          price               qty|\n" +
                "|                                                     |\n" +
                "|Pencil                         2.99                 3|\n" +
                "|Book                          17.99                 1|\n" +
                "|                                                     |\n" +
                "|subtotal                                        26.96|\n" +
                "|tax                                              2.40|\n" +
                "|total                                           29.35|\n" +
                "+-----------------------------------------------------+";
            assertEquals(expectedReceipt, receipt);
        }

        /**
         * 測試收據列印 - 案例3。
         * 
         * 測試場景：
         * - 購物項目：pencil(2)和shirt(1)
         * - 不同商品類別（misc和clothing）
         * - 驗證小計、稅額和總金額的計算
         * - 驗證收據格式的正確性
         */
        @Test
        void shouldPrintReceiptCase3() {
            // 準備測試資料
            Map<String, ItemVO> items = new HashMap<>();
            ItemVO pencil = new ItemVO();
            pencil.setProductName("pencil");
            pencil.setQuantity(2);
            pencil.setLocation(LocationENUM.NY);
            pencil.setCategory("misc");
            
            ItemVO shirt = new ItemVO();
            shirt.setProductName("shirt");
            shirt.setQuantity(1);
            shirt.setLocation(LocationENUM.NY);
            shirt.setCategory("clothing");
            
            items.put("item1", pencil);
            items.put("item2", shirt);

            Map<String, BigDecimal> prices = new HashMap<>();
            prices.put("pencil", new BigDecimal("2.99"));
            prices.put("shirt", new BigDecimal("29.99"));

            // 設置模擬行為
            when(shoppingCart.calSubtotal(anyCollection())).thenReturn(new BigDecimal("35.97"));
            when(shoppingCart.calTax(anyCollection())).thenReturn(new BigDecimal("0.55"));
            when(shoppingCart.calTotal(anyCollection())).thenReturn(new BigDecimal("36.50"));

            // 執行測試
            String receipt = receiptPrinter.getTable(items, prices);

            // 驗證輸出
            String expectedReceipt = 
                "+-----------------------------------------------------+\n" +
                "|item                          price               qty|\n" +
                "|                                                     |\n" +
                "|Shirt                         29.99                 1|\n" +
                "|Pencil                         2.99                 2|\n" +
                "|                                                     |\n" +
                "|subtotal                                        35.97|\n" +
                "|tax                                              0.55|\n" +
                "|total                                           36.50|\n" +
                "+-----------------------------------------------------+";
            assertEquals(expectedReceipt, receipt);
        }
    }
} 