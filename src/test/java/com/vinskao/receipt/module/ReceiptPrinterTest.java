package com.vinskao.receipt.module;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

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
    void setup() {}

    /**
     * 商品名稱格式化功能的測試集合
     */
    @Nested
    class ItemNameFormatterTests {
        
        /**
         * 測試商品名稱格式的轉換
         * 包含底線轉換、大小寫處理、多重底線及特殊情況
         */
        @Test
        void shouldFormatItemNamesCorrectly() {
            // Given
            String underscoreInput = "laptop_computer";
            String uppercaseInput = "LAPTOP";
            String multipleUnderscoreInput = "i_am_vinskao";
            
            // When & Then
            assertEquals("Laptop Computer", receiptPrinter.itemNameFormatter(underscoreInput),
                    "應將底線轉換為空格並將每個單字首字母大寫");
            
            assertEquals("Laptop", receiptPrinter.itemNameFormatter(uppercaseInput),
                    "應將全大寫字串轉換為首字母大寫格式");
            
            assertEquals("I Am Vinskao", receiptPrinter.itemNameFormatter(multipleUnderscoreInput),
                    "應正確處理多個底線的情況");
        }

        /**
         * 測試特殊輸入情況的處理
         */
        @Test
        void shouldHandleSpecialCases() {
            // 測試空字串
            assertEquals("", receiptPrinter.itemNameFormatter(""),
                    "空字串應該回傳空字串");
            
            // 測試 null 值
            assertNull(receiptPrinter.itemNameFormatter(null),
                    "null 輸入應該回傳 null");
        }
    }

    /**
     * 收據表格生成功能的測試集合
     */
    @Nested
    class ReceiptFrameTests {
        
        /**
         * 測試收據表格生成的基本功能
         */
        @Test
        void shouldPrintReceiptFramesWithoutException() {
            assertDoesNotThrow(() -> receiptPrinter.printReceiptFrames(),
                    "收據表格生成不應拋出異常");
        }
    }
} 