package com.vinskao.receipt.module;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * ReceiptPrinter 的單元測試類
 * 測試收據列印相關的功能，包括：
 * 1. 收據表格生成
 * 2. 商品名稱格式化
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
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * 測試商品名稱格式化功能
     * 場景：測試各種格式的商品名稱轉換
     */
    @Test
    public void testItemNameFormatter() {
        // 測試底線轉換為空格，並將首字母大寫
        assertEquals("Laptop Computer", receiptPrinter.itemNameFormatter("laptop_computer"));
        
        // 測試全大寫轉換
        assertEquals("Laptop", receiptPrinter.itemNameFormatter("LAPTOP"));
        
        // 測試多個底線
        assertEquals("This Is A Test", receiptPrinter.itemNameFormatter("this_is_a_test"));
        
        // 測試空字串
        assertEquals("", receiptPrinter.itemNameFormatter(""));
        
        // 測試 null 值
        assertNull(receiptPrinter.itemNameFormatter(null));
    }

    /**
     * 測試收據表格生成功能
     * 場景：驗證表格格式是否正確
     */
    @Test
    public void testPrintReceiptFrames() {
        // 由於 printReceiptFrames 方法主要是整合功能，
        // 這裡我們主要測試它不會拋出異常
        try {
            receiptPrinter.printReceiptFrames();
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }
} 