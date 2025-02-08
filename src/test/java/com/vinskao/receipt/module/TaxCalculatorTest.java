package com.vinskao.receipt.module;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.vinskao.receipt.model.LocationDO;
import com.vinskao.receipt.module.TaxCalculator;


@RunWith(MockitoJUnitRunner.class)
public class TaxCalculatorTest {
    @Mock
    private LocationDO locationsConfig;
    @InjectMocks
    private TaxCalculator taxCalculator;

    @Test
    public void testDetermineTax_WithTax() {
        // 模擬加州 (CA) 一般商品，稅率 9.75%
        String locationCode = "CA";
        String productName = "laptop";
        BigDecimal tax = new BigDecimal("0.0975");
    }
    
} 