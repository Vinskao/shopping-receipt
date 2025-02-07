package com.vinskao.receipt.module;

import java.math.BigDecimal;
import java.util.Map;

import com.vinskao.receipt.config.LocationsConfig;
import com.vinskao.receipt.config.LocationsConfigLoader;
import com.vinskao.receipt.vo.Item;

public class TaxCalculator {
    private LocationsConfig locationsConfig;
    public TaxCalculator(){
        this.locationsConfig = LocationsConfigLoader.load();
    }
    public BigDecimal determineTax(Item item){
        if(item == null || item.getLocation() == null){
            throw new IllegalArgumentException("Item 或 Location 不能為 null");
        }
        
        String location = item.getLocation().name();
        
        
        
        return null;        
    }
}
