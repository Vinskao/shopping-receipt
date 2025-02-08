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
 */
public class ReceiptPrinter {

    /**
     * 讀取 carts.json 與 prices.json，然後依據每個case印出收據框。
     * 將 CartDO 與 PriceDO 中的資料轉換成 Map 結構，
     * 逐一列印各case的名稱與內部收據表格。
     */
    public void printReceiptFrames() {
        try { // 嘗試執行讀取與print作業
            CartDO cartDO = CartsConfigLoader.load(); // 從 carts.json 載入購物車資料，並轉換為 CartDO 物件
            PriceDO priceDO = PricesConfigLoader.load(); // 從 prices.json 載入價格資料，並轉換為 PriceDO 物件

            Map<String, Map<String, ItemVO>> carts = cartDO.getCarts(); // 取得所有case資料，鍵為case名稱，值為購買項目集合
            Map<String, BigDecimal> prices = priceDO.getPrices(); // 取得所有商品的價格資料，鍵為商品名稱，值為該商品價格

            for (String caseKey : carts.keySet()) { // 迭代每個cart中的case
                System.out.println(caseKey); // 輸出case的名稱
                Map<String, ItemVO> items = carts.get(caseKey); // 取得當前case的所有購買項目
                printTable(items, prices); // 印出該case的收據表格
                System.out.println();
            }
        } catch (Exception e) { 
            e.printStackTrace(); // 印出錯誤訊息與堆疊蹤跡，用於除錯
        }
    }

    /**
     * 依據傳入的購買項目資料與商品價格，生成一個收據文字表格。
     * 該表格包含表頭以及每筆購買項目的資料，僅保留最外層的框線，
     * 移除格線間的其他分隔線但保持原有間距。
     *
     * @param items  購買資料，鍵為 purchaseKey，值為單一購買項目的 ItemVO 物件
     * @param prices 商品價格資料，鍵為商品名稱，值為商品價格（以 BigDecimal 表示）
     */
    private void printTable(Map<String, ItemVO> items, Map<String, BigDecimal> prices) {
        int columnWidth = 15; // 定義每個欄位的基礎寬度為 15 字元
        int cellWidth = columnWidth + 2; // 每個 cell 包含左右各一個空白，共計 17 字元寬度
        int numColumns = 3; // 表格設計為三個欄位：item、price 與 qty
        // 計算表格內容區域的總寬度，cellWidth * 欄數，加上欄位間的空格 (numColumns - 1)
        int totalInnerWidth = cellWidth * numColumns + (numColumns - 1);
        // 根據總寬度產生上下框線，使用 '+' 與重複 '-' 號構成
        String border = "+" + repeat("-", totalInnerWidth) + "+";

        System.out.println(border); // 輸出表格的頂部邊框

        // 格式化表頭欄位
        String header1 = String.format("%-" + cellWidth + "s", "item"); // 第一欄：左對齊 "item"
        String header2 = String.format("%" + cellWidth + "s", "price"); // 第二欄：右對齊 "price"
        String header3 = String.format("%" + cellWidth + "s", "qty"); // 第三欄：右對齊 "qty"
        // 將格式化後的表頭連同左右邊框與中間空格輸出
        System.out.println("|" + header1 + " " + header2 + " " + header3 + "|");

        // 輸出一行空白，作為表頭與資料列間的間隔 (維持左右邊框)
        System.out.println("|" + repeat(" ", totalInnerWidth) + "|");

        // 迭代每筆購買資料並格式化列出
        for (ItemVO item : items.values()) {
            String rawName = item.getProductName();
            // 使用 itemNameFormatter 處理產品名稱
            String formattedName = itemNameFormatter(rawName);
            // 格式化產品名稱，固定寬度 cellWidth 並左對齊
            String prodName = String.format("%-" + cellWidth + "s", formattedName);
            // 根據原始產品名稱取得價格，若不存在則預設為 0
            BigDecimal priceBD = prices.getOrDefault(rawName, BigDecimal.ZERO);
            // 格式化價格，固定寬度 cellWidth 並右對齊，顯示兩位小數
            String priceStr = String.format("%" + cellWidth + ".2f", priceBD.doubleValue());
            // 格式化數量，固定寬度 cellWidth 並右對齊
            String qtyStr = String.format("%" + cellWidth + "d", item.getQuantity());
            // 印出當前購買資料行，包含左右邊框與中間空格
            System.out.println("|" + prodName + " " + priceStr + " " + qtyStr + "|");
        }

        System.out.println(border); // 輸出表格的底部邊框
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
