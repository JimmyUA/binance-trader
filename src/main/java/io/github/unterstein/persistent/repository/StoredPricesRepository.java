package io.github.unterstein.persistent.repository;

import io.github.unterstein.persistent.entity.StoredPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.LinkedList;

public interface StoredPricesRepository extends JpaRepository<StoredPrice, Long>{

    @Query("SELECT price FROM stored_prices WHERE id > :start")
    LinkedList<Double> getPortion(@Param("start") Long start);

//    @Query("SELECT storedPrice FROM StoredPrice storedPrice WHERE storedPrice.id > :start")
//    LinkedList<StoredPrice> getFullPricePortion(@Param("start") Long start);
}
