package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.LockRepository;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NamedLockStockService {

    private final LockRepository lockRepository;
    private final StockRepository stockRepository;

    @Transactional
    public void decrease(Long productId, Long quantity) {
        try{

            // 락을 거는 로직과 비즈니스 로직이 혼재되어있다.
            lockRepository.getLock(productId.toString());
            Stock stock = stockRepository.findStockByProductId(productId).orElseThrow();
            stock.decrease(quantity);
            stockRepository.save(stock);
        } finally {
           lockRepository.releaseLock(productId.toString());
        }
    }

    public NamedLockStockService(LockRepository lockRepository, StockRepository stockRepository) {
        this.lockRepository = lockRepository;
        this.stockRepository = stockRepository;
    }
}
