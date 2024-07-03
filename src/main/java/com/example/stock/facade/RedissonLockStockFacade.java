package com.example.stock.facade;

import com.example.stock.service.StockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockStockFacade {

    private RedissonClient redissonClient;
    private StockService stockService;

    public void decrease(Long productId, Long quantity) {
        RLock lock = redissonClient.getLock(productId.toString());
            try {
                Boolean available = lock.tryLock(15, 1, TimeUnit.SECONDS);
                if(!available) {
                    System.out.println("LOCK 획득 실패");
                    return;
                }
                stockService.decrease(productId, quantity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
            lock.unlock();
        }
    }

    public RedissonLockStockFacade(RedissonClient redissonClient, StockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }


}
