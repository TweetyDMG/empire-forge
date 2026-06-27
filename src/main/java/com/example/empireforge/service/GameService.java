package com.example.empireforge.service;

import com.example.empireforge.dto.GameResponse;
import com.example.empireforge.dto.GameStatusResponse;
import com.example.empireforge.entity.Company;
import com.example.empireforge.entity.GameState;
import com.example.empireforge.entity.Player;
import com.example.empireforge.exception.CompanyNotFoundException;
import com.example.empireforge.exception.GameStateNotFoundException;
import com.example.empireforge.repository.CompanyRepository;
import com.example.empireforge.repository.GameStateRepository;
import com.example.empireforge.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    private final PlayerRepository playerRepository;
    private final CompanyRepository companyRepository;
    private final GameStateRepository gameStateRepository;

    public GameService(PlayerRepository playerRepository, CompanyRepository companyRepository, GameStateRepository gameStateRepository) {
        this.playerRepository = playerRepository;
        this.companyRepository = companyRepository;
        this.gameStateRepository = gameStateRepository;
    }

    @Transactional
    public GameResponse startGame(Long chatId, String username, String companyType, String companyName) {
        Player player = playerRepository.findById(chatId)
                .orElseGet(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setChatId(chatId);
                    newPlayer.setUsername(username);
                    log.info("Created new player: chatId={}, username={}", chatId, username);
                    return playerRepository.save(newPlayer);
                });

        if (companyRepository.findByChatId(chatId).isPresent()) {
            return GameResponse.of("У вас уже есть компания! Используйте /status для проверки");
        }

        Company company = new Company();
        company.setChatId(chatId);
        company.setCompanyType(companyType);
        company.setName(companyName);
        company = companyRepository.save(company);

        GameState state = new GameState();
        state.setCompanyId(company.getCompanyId());
        gameStateRepository.save(state);

        log.info("Game started: chatId={}, company={} ({})", chatId, companyName, companyType);

        return GameResponse.of("""
                Игра начата! Вы создали компанию "%s" (%s).
                Баланс: 10,000 у.е.
                Что дальше? /status — просмотр состояния, /endday — завершить день""",
                companyName, companyType);
    }

    public GameStatusResponse getStatus(Long chatId) {
        Company company = companyRepository.findByChatId(chatId)
                .orElseThrow(() -> new CompanyNotFoundException(chatId));

        GameState state = gameStateRepository.findByCompanyId(company.getCompanyId())
                .orElseThrow(() -> new GameStateNotFoundException(company.getCompanyId()));

        return new GameStatusResponse(
                company.getName(),
                company.getCompanyType(),
                state.getCurrentDay(),
                state.getBalance(),
                state.getEmployees(),
                state.getDemand(),
                state.getReputation(),
                state.getResources(),
                state.getCompetition(),
                state.getServiceQuality(),
                state.getEquipmentLevel(),
                state.getTaxRate()
        );
    }

    @Transactional
    public GameResponse endDay(Long chatId) {
        Company company = companyRepository.findByChatId(chatId)
                .orElseThrow(() -> new CompanyNotFoundException(chatId));

        GameState state = gameStateRepository.findByCompanyId(company.getCompanyId())
                .orElseThrow(() -> new GameStateNotFoundException(company.getCompanyId()));

        // Расчёт дохода: спрос * 20 (базовое значение)
        int income = state.getDemand() * 20;
        // Расчёт расходов: зарплата + налоги
        int expenses = state.getEmployees() * 200 + state.getBalance() * state.getTaxRate() / 100;
        int newBalance = state.getBalance() + income - expenses;

        state.setBalance(Math.max(newBalance, 0));
        state.setCurrentDay(state.getCurrentDay() + 1);
        state.setLastUpdated(LocalDateTime.now());
        gameStateRepository.save(state);

        log.info("Day ended: chatId={}, day={}, income={}, expenses={}, balance={}",
                chatId, state.getCurrentDay(), income, expenses, state.getBalance());

        return GameResponse.of("""
                День %d завершён!
                Доход: +%d у.е.
                Расходы: -%d у.е.
                Новый баланс: %d у.е.""",
                state.getCurrentDay(), income, expenses, state.getBalance());
    }
}
