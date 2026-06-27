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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private GameStateRepository gameStateRepository;

    @InjectMocks
    private GameService gameService;

    private static final Long CHAT_ID = 12345L;
    private static final String USERNAME = "test_user";
    private static final String COMPANY_TYPE = "SERVICES";
    private static final String COMPANY_NAME = "TestCompany";

    @BeforeEach
    void setUp() {
        // Общие моки, если нужны
    }

    // ===================== startGame =====================

    @Test
    @DisplayName("startGame: успешное создание новой компании")
    void startGame_success() {
        // given — player exists
        Player player = new Player();
        player.setChatId(CHAT_ID);
        player.setUsername(USERNAME);
        when(playerRepository.findById(CHAT_ID)).thenReturn(Optional.of(player));
        when(companyRepository.findByChatId(CHAT_ID)).thenReturn(Optional.empty());

        Company savedCompany = new Company();
        savedCompany.setCompanyId(1L);
        savedCompany.setChatId(CHAT_ID);
        savedCompany.setCompanyType(COMPANY_TYPE);
        savedCompany.setName(COMPANY_NAME);
        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);

        when(gameStateRepository.save(any(GameState.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        GameResponse response = gameService.startGame(CHAT_ID, USERNAME, COMPANY_TYPE, COMPANY_NAME);

        // then
        assertThat(response.message()).contains("Игра начата!", COMPANY_NAME, COMPANY_TYPE);
        verify(companyRepository).save(any(Company.class));
        verify(gameStateRepository).save(any(GameState.class));
    }

    @Test
    @DisplayName("startGame: новый игрок создаётся автоматически")
    void startGame_createsNewPlayer() {
        // given — player NOT exists, will be created
        when(playerRepository.findById(CHAT_ID)).thenReturn(Optional.empty());
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(companyRepository.findByChatId(CHAT_ID)).thenReturn(Optional.empty());

        Company savedCompany = new Company();
        savedCompany.setCompanyId(2L);
        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);
        when(gameStateRepository.save(any(GameState.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        GameResponse response = gameService.startGame(CHAT_ID, USERNAME, COMPANY_TYPE, COMPANY_NAME);

        // then
        assertThat(response.message()).contains("Игра начата!");
        verify(playerRepository).save(any(Player.class));
        ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository).save(playerCaptor.capture());
        assertThat(playerCaptor.getValue().getChatId()).isEqualTo(CHAT_ID);
        assertThat(playerCaptor.getValue().getUsername()).isEqualTo(USERNAME);
    }

    @Test
    @DisplayName("startGame: ошибка при повторном создании компании")
    void startGame_alreadyHasCompany() {
        // given
        when(playerRepository.findById(CHAT_ID)).thenReturn(Optional.of(new Player()));
        when(companyRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(new Company()));

        // when
        GameResponse response = gameService.startGame(CHAT_ID, USERNAME, COMPANY_TYPE, COMPANY_NAME);

        // then
        assertThat(response.message()).contains("У вас уже есть компания");
        verifyNoMoreInteractions(gameStateRepository);
    }

    // ===================== getStatus =====================

    @Test
    @DisplayName("getStatus: возвращает статус для существующей компании")
    void getStatus_success() {
        // given
        Company company = createCompany(3L);
        when(companyRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(company));

        GameState state = createGameState(3L);
        when(gameStateRepository.findByCompanyId(3L)).thenReturn(Optional.of(state));

        // when
        GameStatusResponse status = gameService.getStatus(CHAT_ID);

        // then
        assertThat(status.companyName()).isEqualTo(COMPANY_NAME);
        assertThat(status.companyType()).isEqualTo(COMPANY_TYPE);
        assertThat(status.balance()).isEqualTo(10000);
        assertThat(status.day()).isEqualTo(1);
        assertThat(status.toTelegramMessage()).contains("10000 у.е.");
    }

    @Test
    @DisplayName("getStatus: CompanyNotFoundException когда компании нет")
    void getStatus_companyNotFound() {
        when(companyRepository.findByChatId(CHAT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.getStatus(CHAT_ID))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining("Компания не найдена");
    }

    @Test
    @DisplayName("getStatus: GameStateNotFoundException когда состояния нет")
    void getStatus_gameStateNotFound() {
        when(companyRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(createCompany(4L)));
        when(gameStateRepository.findByCompanyId(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.getStatus(CHAT_ID))
                .isInstanceOf(GameStateNotFoundException.class)
                .hasMessageContaining("Состояние игры не найдено");
    }

    // ===================== endDay =====================

    @Test
    @DisplayName("endDay: успешное завершение дня")
    void endDay_success() {
        // given
        Company company = createCompany(5L);
        when(companyRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(company));

        GameState state = createGameState(5L);
        state.setBalance(10000);
        state.setEmployees(5);
        state.setDemand(50);
        state.setTaxRate(10);
        when(gameStateRepository.findByCompanyId(5L)).thenReturn(Optional.of(state));

        // when
        GameResponse response = gameService.endDay(CHAT_ID);

        // then
        // income = 50 * 20 = 1000
        // expenses = 5 * 200 + 10000 * 10 / 100 = 1000 + 1000 = 2000
        // new balance = 10000 + 1000 - 2000 = 9000
        assertThat(response.message()).contains("День 2 завершён", "9000 у.е.");
        verify(gameStateRepository).save(state);
        assertThat(state.getCurrentDay()).isEqualTo(2);
        assertThat(state.getBalance()).isEqualTo(9000);
    }

    @Test
    @DisplayName("endDay: баланс не становится отрицательным")
    void endDay_balanceNotNegative() {
        Company company = createCompany(6L);
        when(companyRepository.findByChatId(CHAT_ID)).thenReturn(Optional.of(company));

        GameState state = createGameState(6L);
        state.setBalance(100);      // маленький баланс
        state.setEmployees(50);     // много сотрудников
        state.setDemand(5);         // низкий спрос
        state.setTaxRate(50);       // высокий налог
        when(gameStateRepository.findByCompanyId(6L)).thenReturn(Optional.of(state));

        GameResponse response = gameService.endDay(CHAT_ID);

        // income = 5 * 20 = 100
        // expenses = 50 * 200 + 100 * 50 / 100 = 10000 + 50 = 10050
        // new balance = max(100 + 100 - 10050, 0) = max(-9850, 0) = 0
        assertThat(response.message()).contains("0 у.е.");
        assertThat(state.getBalance()).isEqualTo(0);
    }

    // ===================== helpers =====================

    private Company createCompany(Long companyId) {
        Company company = new Company();
        company.setCompanyId(companyId);
        company.setChatId(CHAT_ID);
        company.setCompanyType(COMPANY_TYPE);
        company.setName(COMPANY_NAME);
        return company;
    }

    private GameState createGameState(Long companyId) {
        GameState state = new GameState();
        state.setStateId(companyId);
        state.setCompanyId(companyId);
        return state; // использует defaults: balance=10000, employees=5, demand=50, ...
    }
}
