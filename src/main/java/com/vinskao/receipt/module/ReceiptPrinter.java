package com.vinskao.receipt.module;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReceiptPrinter {

    public static void main(String[] args) {
        ReceiptPrinter printer = new ReceiptPrinter();
        printer.printReceipts();
    }
    
    /**
     * 讀取 carts.json 與 prices.json ，針對 carts.json 裡每個 case 印出一個收據框
     */
    public void printReceipts() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // 讀取 carts.json，檔案架構為：{ caseName -> { purchaseKey -> Purchase } }
            Map<String, Map<String, Purchase>> carts = mapper.readValue(
                new File("src/main/resources/carts.json"),
                new TypeReference<Map<String, Map<String, Purchase>>>(){}
            );
            
            // 讀取 prices.json，檔案架構為：{ productName -> price }
            Map<String, Double> prices = mapper.readValue(
                new File("src/main/resources/prices.json"),
                new TypeReference<Map<String, Double>>(){}
            );
            
            // 每個 case 就印出一個框
            for (String caseKey : carts.keySet()) {
                System.out.println("Case: " + caseKey);
                Map<String, Purchase> purchases = carts.get(caseKey);
                printTable(purchases, prices);
                System.out.println(); // case 間留白
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 依據傳入的購買資料與價格，產生一個收據文字框，內容包含 header 與每筆資料
     *
     * @param purchases 購買資料，以 purchaseKey 為 key，Purchase 為 value
     * @param prices    商品價格，以 productName 為 key
     */
    private void printTable(Map<String, Purchase> purchases, Map<String, Double> prices) {
        // 根據 header 與資料計算各欄位的寬度
        int itemWidth = "item".length();
        int priceWidth = "price".length();
        int qtyWidth = "qty".length();
        
        for (Purchase p : purchases.values()) {
            itemWidth = Math.max(itemWidth, p.getProductName().length());
            String priceStr = prices.containsKey(p.getProductName())
                    ? String.format("%.2f", prices.get(p.getProductName()))
                    : "0.00";
            priceWidth = Math.max(priceWidth, priceStr.length());
            qtyWidth = Math.max(qtyWidth, String.valueOf(p.getQuantity()).length());
        }
        
        // 加上左右補白（預留 2 個空白）
        itemWidth += 2;
        priceWidth += 2;
        qtyWidth += 2;
        
        // 組成水平框線
        String border = "+" + repeat("-", itemWidth + 2)
                + "+" + repeat("-", priceWidth + 2)
                + "+" + repeat("-", qtyWidth + 2) + "+";
        
        // 印出整體框，第一行為框線，接著 header，再框線，然後是每筆資料，最後再印框線
        System.out.println(border);
        System.out.printf("| %-" + itemWidth + "s | %-" + priceWidth + "s | %-" + qtyWidth + "s |\n",
                "item", "price", "qty");
        System.out.println(border);
        
        for (Purchase purchase : purchases.values()) {
            String productName = purchase.getProductName();
            double price = prices.getOrDefault(productName, 0.00);
            int quantity = purchase.getQuantity();
            System.out.printf("| %-" + itemWidth + "s | %" + priceWidth + ".2f | %" + qtyWidth + "d |\n",
                    productName, price, quantity);
        }
        System.out.println(border);
    }
    
    /**
     * 簡單重複字串的工具函式
     * @param s 重複的字串
     * @param n 次數
     * @return 重複 n 次後的結果
     */
    private String repeat(String s, int n) {
        return new String(new char[n]).replace("\0", s);
    }
    
    /**
     * Purchase 內部類別用來映射 carts.json 中的購買項目
     */
    public static class Purchase {
        private String productName;
        private int quantity;
        private String category;
        
        // 預設建構子（必要時給 Jackson 使用）
        public Purchase() {
        }
        
        public String getProductName() {
            return productName;
        }
        
        public void setProductName(String productName) {
            this.productName = productName;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
    }
}
