package com.vinskao.receipt.model;

import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 * CartDO 代表購物車的資料物件，用以封裝從 carts.json 中讀取的資料。
 * @author VinsKao
 */
public class CartDO {
    // 儲存所有購物車case，key為case名稱，value為該case內項目的 Map
    private Map<String, Map<String, ItemVO>> carts = new HashMap<>();

    public CartDO() {
    }

    /**
     * 依據傳入的購物車資料建立 CartDO 物件。
     *
     * @param carts 包含各購物車case資料的 Map，key 為case名稱，value 為該case內購買項目的 Map
     */
    public CartDO(Map<String, Map<String, ItemVO>> carts) {
        this.carts = carts;
    }

    public Map<String, Map<String, ItemVO>> getCarts() {
        return this.carts;
    }

    public void setCarts(Map<String, Map<String, ItemVO>> carts) {
        this.carts = carts;
    }

    public CartDO carts(Map<String, Map<String, ItemVO>> carts) {
        setCarts(carts); 
        return this; 
    }

    /**
     * 當 JSON 中有額外的屬性時，將呼叫此方法新增購物車case資料。
     *
     * @param caseKey 購物車case的 key
     * @param cart    該case對應的購買項目集合，key 為`purchase + ${i}`(第i個購買)，value 為 ItemVO 物件
     */
    @JsonAnySetter
    public void addCart(String caseKey, Map<String, ItemVO> cart) {
        this.carts.put(caseKey, cart); // 將傳入的case資料加入 carts
    }
}
