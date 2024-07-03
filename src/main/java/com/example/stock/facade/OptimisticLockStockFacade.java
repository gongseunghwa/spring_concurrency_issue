package com.example.stock.facade;

import com.example.stock.service.OptimisticStockService;
import org.springframework.stereotype.Component;

@Component
public class OptimisticLockStockFacade {

    private final OptimisticStockService stockService;

    public void decrease(Long productId, Long quantity) throws InterruptedException {
        while (true) {
            try{
                stockService.decrease(productId, quantity);
                break;
            } catch (Exception e) {
                Thread.sleep(500);
            }
        }
    }
    public OptimisticLockStockFacade(OptimisticStockService stockService) {
        this.stockService = stockService;
    }
}
