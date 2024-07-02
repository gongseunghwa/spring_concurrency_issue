package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class StockService {
    private final StockRepository stockRepository;

    @Transactional
    public synchronized void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findStockByProductId(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.save(stock);

    }

}
