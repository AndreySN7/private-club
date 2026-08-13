package com.club.private_club.controller;

import com.club.private_club.dto.ClubMemberDto;
import com.club.private_club.entity.ClubMember;
import com.club.private_club.entity.QRCode;
import com.club.private_club.repository.ClubMemberRepository;
import com.club.private_club.repository.QRCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Testcontainers
class ClubMemberControllerIT {

	@Container
	static final PostgreSQLContainer POSTGRESQL_CONTAINER =
				new PostgreSQLContainer("postgres:latest");

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
		registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
	}

	@Autowired
	private ClubMemberRepository clubMemberRepository;
	@Autowired
	private QRCodeRepository qrCodeRepository;
	@Autowired
	TestRestTemplate testRestTemplate;

	@BeforeEach
	void setup() {
		qrCodeRepository.deleteAll();
		clubMemberRepository.deleteAll();
	}

	@Test
	@DisplayName("id найден, qr активен -> возвращаем участника, деактивируем qr, выдаем новый qr")
	void findByClubMember_whenIdFoundAndQrActive_thenReturnParticipantAndDeactivateTheQrAndAddNewQr() {
		ClubMember clubMember = new ClubMember(null, "Bill", "Smith");
		ClubMember savedMember = clubMemberRepository.save(clubMember);
		QRCode qrCode = new QRCode(null, savedMember.getId(), UUID.randomUUID(), true);
		QRCode savedQr = qrCodeRepository.save(qrCode);

		ResponseEntity<ClubMemberDto> response = testRestTemplate.exchange(
					buildUrl("/" + savedQr.getId()),
					HttpMethod.GET,
					null,
					ClubMemberDto.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody())
					.isNotNull()
					.hasFieldOrPropertyWithValue("firstName", response.getBody().firstName())
					.hasFieldOrPropertyWithValue("lastName", response.getBody().lastName());
		Boolean isActiveQr = qrCodeRepository.findById(1L).get().getIsActive();
		assertThat(isActiveQr).isFalse();
		List<QRCode> qrCodes = qrCodeRepository.findAll();
		assertThat(qrCodes)
					.hasSize(2)
					.extracting(QRCode::getId)
					.contains(1L, 2L);
	}

	private String buildUrl(String path) {
		return "/api/v1/club_member" + path;
	}


}