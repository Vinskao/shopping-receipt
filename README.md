# shopping-receipt

shopping-receipt 是一個 Maven 專案，模擬稅金計算、收據列印以及購物車總額計算功能。主要模組包括：

```
shopping-receipt
├── .gitignore
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── vinskao
    │   │           └── receipt
    │   │               ├── Application.java
    │   │               ├── config
    │   │               │   ├── CartsConfigLoader.java
    │   │               │   ├── LocationsConfigLoader.java
    │   │               │   └── PricesConfigLoader.java
    │   │               ├── model
    │   │               │   ├── CartDO.java
    │   │               │   ├── ItemVO.java
    │   │               │   ├── LocationDO.java
    │   │               │   ├── LocationENUM.java
    │   │               │   └── PriceDO.java
    │   │               └── module
    │   │                   ├── ReceiptPrinter.java
    │   │                   ├── ShoppingCart.java
    │   │                   └── TaxCalculator.java
    │   └── resources
    │       ├── carts.json
    │       ├── locations.json
    │       └── prices.json
    └── test
        └── java
            └── com
                └── vinskao
                    └── receipt
                        └── module
                            ├── ReceiptPrinterTest.java
                            ├── ShoppingCartTest.java
                            └── TaxCalculatorTest.java
```

## 啟動說明

1. 確保已安裝 Maven 和 Java 17
2. 在專案根目錄執行：
   ```bash
   mvn clean install
   java -jar target/shopping-receipt-1.0-jar-with-dependencies.jar
   ```

## JSON 檔案配置說明

專案中的 JSON 檔案可依需求自行修改：

### prices.json
- 用於設定商品價格

### locations.json
- 用於設定不同地區的稅率

### carts.json
- 用於設定購物車內容