package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptimisticStockService {
    private final StockRepository stockRepository;

    @Transactional
    public void decrease(Long productId, Long quantity) {
        Stock stock = stockRepository.findStockOptimisticLockByProductId(productId).get();
        stock.decrease(quantity);
        stockRepository.save(stock);
    }
    public OptimisticStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }
}
