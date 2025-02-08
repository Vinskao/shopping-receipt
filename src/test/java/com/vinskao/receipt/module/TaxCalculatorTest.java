package com.vinskao.receipt.module;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.vinskao.receipt.model.ItemVO;
import com.vinskao.receipt.model.LocationDO;
import com.vinskao.receipt.model.LocationENUM;
import com.vinskao.receipt.model.PriceDO;

@RunWith(MockitoJUnitRunner.class)
public class TaxCalculatorTest {
    @Mock
    private LocationDO locationsConfig;
    
    @Mock
    private PriceDO pricesConfig;
    
    @InjectMocks
    private TaxCalculator taxCalculator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDetermineTax_WithTax() {
        // 模擬加州 (CA) 一般商品，稅率 9.75%
        LocationENUM locationCode = LocationENUM.CA;
        String productName = "laptop";
        BigDecimal expectedTax = new BigDecimal("0.0975");

        // 模擬返回的稅率
        when(locationsConfig.getTaxRate(locationCode.name())).thenReturn(expectedTax);
        when(locationsConfig.getExemptCategories(locationCode.name())).thenReturn(Collections.emptyList());
        
        // 準備一個有稅 ItemVO
        ItemVO item = new ItemVO();
        item.setLocation(locationCode);
        item.setCategory("electronics");
        item.setProductName(productName);

        
        BigDecimal tax = taxCalculator.determineTax(item);
        assertEquals(expectedTax, tax);
    }

    @Test
    public void testDetermineTax_WithoutTax() {
        // 模擬CA 免稅商品
        LocationENUM locationCode = LocationENUM.CA;
        String productName = "apple";
        BigDecimal expectedTax = BigDecimal.ZERO;

        // 模擬返回的稅率與免稅商品類別
        when(locationsConfig.getTaxRate(locationCode.name())).thenReturn(new BigDecimal("0.0975"));
        when(locationsConfig.getExemptCategories(locationCode.name())).thenReturn(Arrays.asList("food"));

        // 準備一個免稅 ItemVO
        ItemVO item = new ItemVO();
        item.setLocation(locationCode);
        item.setCategory("food");
        item.setProductName(productName);

        
        BigDecimal tax = taxCalculator.determineTax(item);
        assertEquals(expectedTax, tax);
    }

    @Test
    public void testCalculateTotalTax() {
        // 模擬商品與稅率
        LocationENUM locationCode = LocationENUM.CA;
        String product1 = "laptop";
        String product2 = "apple";
        
        // 預期稅額
        BigDecimal expectedTax1 = new BigDecimal("9.75"); // Laptop tax
        BigDecimal expectedTax2 = BigDecimal.ZERO; // Apple tax (food exempt)

        // 模擬返回的稅率與免稅商品類別
        when(locationsConfig.getTaxRate(locationCode.name())).thenReturn(new BigDecimal("0.0975"));
        when(locationsConfig.getExemptCategories(locationCode.name())).thenReturn(Arrays.asList("food"));

        // 模擬價格設定
        when(pricesConfig.getPrices().get(product1)).thenReturn(new BigDecimal("100"));
        when(pricesConfig.getPrices().get(product2)).thenReturn(new BigDecimal("1"));

        // 準備商品列表
        ItemVO item1 = new ItemVO();
        item1.setLocation(locationCode);
        item1.setCategory("electronics");
        item1.setProductName(product1);
        item1.setPrice(new BigDecimal("100"));
        item1.setQuantity(1);

        ItemVO item2 = new ItemVO();
        item2.setLocation(locationCode);
        item2.setCategory("food");
        item2.setProductName(product2);
        item2.setPrice(new BigDecimal("1"));
        item2.setQuantity(2);

        // 準備商品列表
        Collection<ItemVO> items = Arrays.asList(item1, item2);

        // 調用方法並進行斷言
        BigDecimal totalTax = taxCalculator.calculateTotalTax(items);
        BigDecimal expectedTotalTax = expectedTax1.add(expectedTax2);
        assertEquals(expectedTotalTax, totalTax);
    }

    
} 