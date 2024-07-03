package com.example.stock.repository;

import com.example.stock.domain.Stock;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID>{
    Optional<Stock> findStockByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.productId = :productId")
    Optional<Stock> findStockPessimisticLockByProductId(Long productId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select s from Stock s where s.productId = :productId")
    Optional<Stock> findStockOptimisticLockByProductId(Long productId);
}
