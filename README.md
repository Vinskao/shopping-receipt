# shopping-receipt

shopping-receipt 是一個 Maven 專案，模擬稅金計算、收據列印以及購物車總額計算功能。主要模組包括：

```
shopping-receipt
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── vinskao
    │   │           └── receipt
    │   │               ├── config
    │   │               │   └── ...
    │   │               ├── model
    │   │               │   ├── ItemVO.java
    │   │               │   └── LocationENUM.java
    │   │               └── module
    │   │                   ├── TaxCalculator.java
    │   │                   ├── ShoppingCart.java
    │   │                   └── ReceiptPrinter.java
    │   └── resources
    │       ├── carts.json
    │       ├── prices.json
    │       └── locations.json
    └── test
        └── java
            └── com
                └── vinskao
                    └── receipt
                        └── module
                            └── TaxCalculatorTest.java
```