package io.github.unterstein.persistent.repository;

import io.github.unterstein.persistent.entity.Spread;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpreadRepository extends JpaRepository<Spread, Long> {
}
