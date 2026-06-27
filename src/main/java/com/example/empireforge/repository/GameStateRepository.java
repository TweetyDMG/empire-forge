package com.example.empireforge.repository;

import com.example.empireforge.entity.GameState;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GameStateRepository extends JpaRepository<GameState, Long> {
    Optional<GameState> findByCompanyId(Long companyId);
}