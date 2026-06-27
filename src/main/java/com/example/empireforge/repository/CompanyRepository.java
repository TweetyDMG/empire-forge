package com.example.empireforge.repository;

import com.example.empireforge.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByChatId(Long chatId);
}