package io.github.unterstein.persistent.repository;

import io.github.unterstein.persistent.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long>{

}
