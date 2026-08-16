package com.club.private_club.controller;

import com.club.private_club.entity.ClubMember;
import com.club.private_club.entity.QRCode;
import com.club.private_club.repository.ClubMemberRepository;
import com.club.private_club.repository.QRCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Slf4j
class ClubMemberControllerIT {

	@Container
	static final PostgreSQLContainer POSTGRESQL_CONTAINER =
				new PostgreSQLContainer("postgres:latest")
							.withDatabaseName("test_db")
							.withUsername("test")
							.withPassword("test");

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
		registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ClubMemberRepository clubMemberRepository;
	@Autowired
	private QRCodeRepository qrCodeRepository;

	@BeforeEach
	void setup() {
		qrCodeRepository.deleteAll();
		clubMemberRepository.deleteAll();
	}

	@Test
	@DisplayName("id найден, qr активен -> возвращаем участника, деактивируем qr, выдаем новый qr")
	void getClubMember_whenIdFoundAndQrActive_thenReturnParticipantAndDeactivateTheQrAndAddNewQr() throws Exception {
		ClubMember savedMember = clubMemberRepository.save(
					new ClubMember(null, "Bill", "Smith"));
		QRCode savedQr = qrCodeRepository.save(
					new QRCode(null, savedMember.getId(), UUID.randomUUID(), true));

		mockMvc.perform(get("/api/v1/club_member/" + savedQr.getId()))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.firstName").value("Bill"))
					.andExpect(jsonPath("$.lastName").value("Smith"));

		Optional<QRCode> qrOptional = qrCodeRepository.findById(savedQr.getId());
		assertThat(qrOptional).isPresent();
		assertThat(qrOptional.get().getIsActive()).isFalse();

		List<QRCode> qrCodes = qrCodeRepository.findAll();
		assertThat(qrCodes).hasSize(2);
		assertThat(qrCodes.stream()
					.filter(i -> !i.getId().equals(savedQr.getId()))
					.findFirst()
					.orElseThrow())
					.extracting(QRCode::getIsActive)
					.isEqualTo(true);
	}

	@Test
	@DisplayName("id найден и qr не активен -> вход запрещен")
	void getClubMember_whenIdFoundAndQrNotActive_thenThrowEntryIsNotAllowed() throws Exception {
		ClubMember savedMember = clubMemberRepository.save(
					new ClubMember(null, "Bob", "Dilan"));
		QRCode savedQr = qrCodeRepository.save(
					new QRCode(null, savedMember.getId(), UUID.randomUUID(), false));

		mockMvc.perform(get("/api/v1/club_member/" + savedQr.getId()))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.message").value("Entry is not allowed."));
	}

	@Test
	@DisplayName("id не найден -> вход запрещен")
	void getClubMember_whenIdNotFound_thenThrowEntryIsNotAllowed() throws Exception {
		mockMvc.perform(get("/api/v1/club_member/1"))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.message").value("Entry is not allowed."));
	}

	@Test
	@DisplayName("id null -> внутренняя ошибка сервера")
	void getClubMember_whenIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(get("/api/v1/club_member"))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.message").value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("буквы (анг) -> внутренняя ошибка сервера")
	void findByClubMember_whenLetters_thenInternalServerError() throws Exception {
		mockMvc.perform(get("/api/v1/club_member/a"))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.message").value("Внутренняя ошибка сервера"));
	}
}