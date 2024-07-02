package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PessimisticStockService {

    private final StockRepository stockRepository;

    @Transactional
    public void decrease(Long productId, Long quantity) {
        Stock stock = stockRepository.findStockPessimisticLockByProductId(productId).get();
        stock.decrease(quantity);
        stockRepository.save(stock);
    }

    public PessimisticStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }
}
