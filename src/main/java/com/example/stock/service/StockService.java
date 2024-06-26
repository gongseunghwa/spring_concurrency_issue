package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import lombok.*;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class StockService {
    private final StockRepository stockRepository;
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findStockByProductId(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.save(stock);

    }

}
