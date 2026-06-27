package com.example.empireforge.repository;

import com.example.empireforge.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
