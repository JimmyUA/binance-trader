package io.github.unterstein.persistent.repository;

import io.github.unterstein.persistent.entity.Amplitude;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmplitudeRepository extends JpaRepository<Amplitude, Long> {
}
