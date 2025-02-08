package com.vinskao.receipt.module;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinskao.receipt.model.ItemVO;

public class ReceiptPrinter {
    /**
     * 讀取 carts.json 與 prices.json ，針對 carts.json 裡每個 case 印出一個收據框
     */
    public void printReceipts() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // 讀取 carts.json，檔案架構為：{ caseName -> { purchaseKey -> ItemVO } }
            Map<String, Map<String, ItemVO>> carts = mapper.readValue(
                new File("src/main/resources/carts.json"),
                new TypeReference<Map<String, Map<String, ItemVO>>>(){}
            );
            
            // 讀取 prices.json，檔案架構為：{ productName -> price }
            Map<String, Double> prices = mapper.readValue(
                new File("src/main/resources/prices.json"),
                new TypeReference<Map<String, Double>>(){}
            );
            
            // 每個 case 就印出一個框
            for (String caseKey : carts.keySet()) {
                System.out.println("Case: " + caseKey);
                Map<String, ItemVO> items = carts.get(caseKey);
                printTable(items, prices);
                System.out.println(); // case 間留白
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 依據傳入的購買資料與價格，產生一個收據文字框，內容包含 header 與每筆資料
     *
     * @param items 購買資料，以 purchaseKey 為 key，Purchase 為 value
     * @param prices    商品價格，以 productName 為 key
     */
    private void printTable(Map<String, ItemVO> items, Map<String, Double> prices) {
        int columnWidth = 15;
        // 組成水平框線
        String border = "+" + repeat("-", columnWidth + 2)
        + "+" + repeat("-", columnWidth + 2)
        + "+" + repeat("-", columnWidth + 2) + "+";
        // 印出整體框，第一行為框線，接著 header，再框線，然後是每筆資料，最後再印框線
        System.out.println(border);
        System.out.printf("| %-" + columnWidth + "s | %" + columnWidth + "s | %" + columnWidth + "s | ", "item", "price", "qty");
        System.out.println(border);
        
        for (ItemVO i : items.values()) {
            String productName = i.getProductName();
            String priceStr = prices.containsKey(i.getProductName())
                    ? String.format("%.2f", prices.get(i.getProductName()))
                    : "0.00";
        }
        
        for (ItemVO ItemVO : items.values()) {
            String productName = ItemVO.getProductName();
            double price = prices.getOrDefault(productName, 0.00);
            int quantity = ItemVO.getQuantity();
            System.out.printf("| %-" + columnWidth + "s | %" + columnWidth + ".2f | %" + columnWidth + "d | ", productName, price, quantity);
        }
        System.out.println(border);
    }
    
    /**
     * 重複字串的工具
     * @param s 重複的字串
     * @param n 次數
     * @return 重複 n 次後的結果
     */
    private String repeat(String s, int n) {
        return new String(new char[n]).replace("\0", s);
    }
    
}
