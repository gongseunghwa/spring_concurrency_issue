package com.example.stock.facade;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade {

    private final RedisLockRepository lockRepository;
    private final StockService stockService;

    public void decrease(Long productId, Long quantity) throws InterruptedException {
        while (!lockRepository.lock(productId)) {
            Thread.sleep(100);
        }

        try{
            stockService.decrease(productId, quantity);
        } finally {
            lockRepository.unlock(productId);
        }
    }

    public LettuceLockStockFacade(RedisLockRepository lockRepository, StockService stockService) {
        this.lockRepository = lockRepository;
        this.stockService = stockService;
    }
}
