package com.vinskao.receipt;

import com.vinskao.receipt.module.ReceiptPrinter;

public class Application {
    public static void main(String[] args) {
        ReceiptPrinter printer = new ReceiptPrinter();
        printer.printReceipts();
    }
}
