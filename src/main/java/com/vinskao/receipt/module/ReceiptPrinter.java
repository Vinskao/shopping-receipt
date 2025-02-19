package com.vinskao.receipt.module;

import java.math.BigDecimal;
import java.util.Map;

import com.vinskao.receipt.config.CartsConfigLoader;
import com.vinskao.receipt.config.PricesConfigLoader;
import com.vinskao.receipt.model.CartDO;
import com.vinskao.receipt.model.ItemVO;
import com.vinskao.receipt.model.PriceDO;

/**
 * 收據印表機類別，用於生成並印出購物車中每個 case 的收據框。
 * 透過 ConfigLoader 讀取 carts.json 與 prices.json 檔案，
 * 然後根據讀取的資料印出每個case的收據表格。
 * @author VinsKao
 */
public class ReceiptPrinter {
    private ShoppingCart shoppingCart;

    public ReceiptPrinter(){
        this.shoppingCart = new ShoppingCart();
    }
    
    /**
     * 讀取 carts.json 與 prices.json，然後依據每個case印出收據框。
     * 將 CartDO 與 PriceDO 中的資料轉換成 Map 結構，
     * 逐一列印各case的名稱與內部收據表格。
     */
    public void printReceiptFrames() {
        try { 
            // 從 carts.json 載入購物車資料，並轉換為 CartDO 物件
            CartDO cartDO = CartsConfigLoader.load(); 
            // 從 prices.json 載入價格資料，並轉換為 PriceDO 物件
            PriceDO priceDO = PricesConfigLoader.load(); 

            // 取得所有case資料，key為case名稱，值為購買項目集合
            Map<String, Map<String, ItemVO>> carts = cartDO.getCarts(); 
            // 取得所有商品的價格資料，key為商品名稱，值為該商品價格
            Map<String, BigDecimal> prices = priceDO.getPrices(); 

            // 迭代每個cart中的case
            for (String caseKey : carts.keySet()) { 
                // case的名稱
                System.out.println(caseKey); 
                // 取得當前case的所有購買項目
                Map<String, ItemVO> items = carts.get(caseKey); 
                // 印出該case的收據表格
                printTable(items, prices); 
                System.out.println();
            }
        } catch (Exception e) { 
            e.printStackTrace();
        }
    }

    /**
     * 依據傳入的購買項目資料與商品價格，生成一個收據文字表格。
     * 該表格包含表頭以及每筆購買項目的資料，僅保留最外層的框線，
     * 移除格線間的其他分隔線但保持原有間距。
     *
     * @param items  購買資料，key為 purchaseKey，值為單一購買項目的 ItemVO 物件
     * @param prices 商品價格資料，key為商品名稱，值為商品價格（以 BigDecimal 表示）
     */
    String getTable(Map<String, ItemVO> items, Map<String, BigDecimal> prices) {
        int columnWidth = 15; // 基礎寬度 15 字元
        int cellWidth = columnWidth + 2; // 每個 cell 包含左右各一個空白，共 17 字元
        int numColumns = 3; // 表格設計為三個欄位：item、price 與 qty
        // 計算表格內容區域的總寬度，cellWidth * 欄數，加上欄位間的空格 (numColumns - 1)
        int totalInnerWidth = cellWidth * numColumns + (numColumns - 1);
        // 根據總寬度產生上下框線，使用 '+' 與重複 '-' 號構成
        String border = "+" + repeat("-", totalInnerWidth) + "+";

        StringBuilder table = new StringBuilder();
        // 輸出表格的頂部邊框
        table.append(border).append("\n");

        // 格式化表頭欄位
        String header1 = String.format("%-" + cellWidth + "s", "item"); // 第一欄：左對齊 "item"
        String header2 = String.format("%" + cellWidth + "s", "price"); // 第二欄：右對齊 "price"
        String header3 = String.format("%" + cellWidth + "s", "qty"); // 第三欄：右對齊 "qty"

        // 將格式化後的表頭連同左右邊框與中間空格輸出
        table.append("|").append(header1).append(" ").append(header2).append(" ").append(header3).append("|").append("\n");
        // 輸出一行空白，作為表頭與資料列間的間隔 (維持左右邊框)
        table.append("|").append(repeat(" ", totalInnerWidth)).append("|").append("\n");

        BigDecimal subtotal = BigDecimal.ZERO;

        // 為避免 TaxCalculator 因 item.getLocation() 為 null 拋出異常，
        // 先遍歷所有 ItemVO，若 location 為 null，則設定NA。
        for (ItemVO item : items.values()) {
            if (item.getLocation() == null) {
                item.setLocation(com.vinskao.receipt.model.LocationENUM.NA);
            }
        }

        // 迭代每筆購買資料並格式化列出
        for (ItemVO item : items.values()) {
            String formattedName = itemNameFormatter(item.getProductName()); // 格式化商品名稱
            BigDecimal price = prices.getOrDefault(item.getProductName(), BigDecimal.ZERO); // 取得商品價格，若無則設為0
            int quantity = item.getQuantity(); // 取得購買數量
            
            String prodName = String.format("%-" + cellWidth + "s", formattedName); // 格式化商品名稱欄位
            String priceStr = String.format("%" + cellWidth + ".2f", price.doubleValue()); // 格式化價格欄位，保留兩位小數
            String qtyStr = String.format("%" + cellWidth + "d", quantity); // 格式化數量欄位
            table.append("|").append(prodName).append(" ").append(priceStr).append(" ").append(qtyStr).append("|").append("\n");
            subtotal = shoppingCart.calSubtotal(items.values());
        }

        BigDecimal tax = shoppingCart.calTax(items.values()); // 計算稅金
        // 防止calTax返回null導致NullPointerException
        if (tax == null) {
            tax = BigDecimal.ZERO;
        }
        BigDecimal total = shoppingCart.calTotal(items.values()); // 計算總金額

        table.append("|").append(repeat(" ", totalInnerWidth)).append("|").append("\n");
        table.append(String.format("|%-" + cellWidth + "s %" + (totalInnerWidth - cellWidth - 1) + ".2f|\n", "subtotal", subtotal)); // 保留後兩位數
        table.append(String.format("|%-" + cellWidth + "s %" + (totalInnerWidth - cellWidth - 1) + ".2f|\n", "tax", tax)); // 保留後兩位數
        table.append(String.format("|%-" + cellWidth + "s %" + (totalInnerWidth - cellWidth - 1) + ".2f|\n", "total", total)); // 保留後兩位數
        // 輸出表格的底部邊框
        table.append(border);
        return table.toString();
    }

    private void printTable(Map<String, ItemVO> items, Map<String, BigDecimal> prices) {
        System.out.println(getTable(items, prices));
    }

    /**
     * 工具方法：將指定字串重複 n 次並返回結果。
     *
     * @param s 需要重複的字串
     * @param n 重複的次數
     * @return 由字串 s 重複 n 次所構成的新字串
     */
    private String repeat(String s, int n) {
        // 利用新的 char 陣列建立一個字串，再使用 replace 方法將空字符替換成指定字串 s 
        return new String(new char[n]).replace("\0", s);
    }

    /**
     * 將商品名稱格式化，將底線轉換為空格，首字母轉大寫。
     *
     * @param name 原始商品名稱，可能包含底線作為分隔符
     * @return 格式化後的商品名稱；若傳入 null 或空字串則直接返回原值
     */
    public String itemNameFormatter(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        
        // 先全部轉為小寫
        name = name.toLowerCase();

        StringBuilder formattedName = new StringBuilder();
        boolean capitalizeNext = true;

        // 遍歷字串 name 的每個字元
        for (int i = 0; i < name.length(); i++) {
            char currentChar = name.charAt(i);
        
            if (currentChar == '_') {
                formattedName.append(' ');
                // Flag下一個字元應轉為大寫
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    formattedName.append(Character.toUpperCase(currentChar));
                    // 中斷大寫狀態
                    capitalizeNext = false;
                } else {
                    // 繼續用原本的字元
                    formattedName.append(currentChar);
                }
            }
        }
        return formattedName.toString();
    }
}
