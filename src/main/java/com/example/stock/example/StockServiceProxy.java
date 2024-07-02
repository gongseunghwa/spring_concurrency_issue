package com.example.stock.example;

import com.example.stock.service.StockService;

public class StockServiceProxy {
    private final StockService stockService;
    private final Transaction transaction;
    public void proxy() {
        try{
            transaction.start();
            stockService.decrease(1l,1l);
        }catch (Exception e) {
            transaction.commit();
        }
    }

    public StockServiceProxy(StockService stockService, Transaction transaction) {
        this.stockService = stockService;
        this.transaction = transaction;
    }

}
