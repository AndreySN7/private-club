package com.club.private_club.controller;

import com.club.private_club.entity.ClubMember;
import com.club.private_club.entity.QRCode;
import com.club.private_club.repository.ClubMemberRepository;
import com.club.private_club.repository.QRCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QRCodeControllerIT extends AbstractIntegrationTest {

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
	@DisplayName("memberId null -> исключение 'внутренняя ошибка сервера'")
	void addClubMemberQR_whenMemberIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(post("/api/v1/qr/")
								.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.message").value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("memberId не найден -> new EntityNotFoundException('Club member is not found.')")
	void addClubMemberQR_whenMemberIdNotFound_thenThrowEntityNotFound() throws Exception {
		int id = 1;
		mockMvc.perform(post("/api/v1/qr/" + id)
								.contentType(MediaType.APPLICATION_JSON)
								.content("{}"))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.message")
								.value("Club member with id = " + id + " is not found."));
	}

	@Test
	@DisplayName("memberId найден, count null -> создаются 3и новых qr для memberId")
	void addClubMemberQR_whenMemberIdFoundAndCountNull_then3NewQrCreatedForMemberId() throws Exception {
		ClubMember clubMember = clubMemberRepository.save(
					new ClubMember(null, "Tom", "Ford"));

		mockMvc.perform(post("/api/v1/qr/" + clubMember.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"count\":null}"))
					.andExpect(status().isCreated());

		List<QRCode> qrCodes = qrCodeRepository.findAll();

		assertThat(qrCodes).hasSize(3);
		assertThat(qrCodes)
					.extracting(QRCode::getMemberId)
					.containsOnly(clubMember.getId());
		assertThat(qrCodes)
					.extracting(QRCode::getIsActive)
					.containsOnly(true);
	}

	@Test
	@DisplayName("memberId найден, count больше 10 -> создаются 10 новых qr для memberId")
	void addClubMemberQR_whenMemberIdFoundAndCountMoreThan10_then10NewQrCreatedForMemberId() throws Exception {
		ClubMember clubMember = clubMemberRepository.save(
					new ClubMember(null, "Bill", "Rock"));

		mockMvc.perform(post("/api/v1/qr/" + clubMember.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"count\": 20}"))
					.andExpect(status().isCreated());

		List<QRCode> qrCodes = qrCodeRepository.findAll();

		assertThat(qrCodes).hasSize(10);
		assertThat(qrCodes)
					.extracting(QRCode::getIsActive)
					.containsOnly(true);
		assertThat(qrCodes)
					.extracting(QRCode::getMemberId)
					.containsOnly(clubMember.getId());
	}

	@Test
	@DisplayName("memberId найден, count меньше 10 -> создаются count новых qr для memberId")
	void addClubMemberQR_whenMemberIdFoundAndCountLessThan10_thenCountNewQrCreatedForMemberId() throws Exception {
		ClubMember clubMember = clubMemberRepository.save(
					new ClubMember(null, "Bill", "Rock"));

		mockMvc.perform(post("/api/v1/qr/" + clubMember.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"count\": 2}"))
					.andExpect(status().isCreated());

		List<QRCode> qrCodes = qrCodeRepository.findAll();

		assertThat(qrCodes).hasSize(2);
		assertThat(qrCodes)
					.extracting(QRCode::getIsActive)
					.containsOnly(true);
		assertThat(qrCodes)
					.extracting(QRCode::getMemberId)
					.containsOnly(clubMember.getId());
	}

	@Test
	@DisplayName("qrId null -> исключение 'внутренняя ошибка сервера'")
	void deleteClubMemberQR_whenQrIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(delete("/api/v1/qr/")
								.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.message").value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("qrId не найден -> new EntityNotFoundException('QR-code is not found.')")
	void deleteClubMemberQR_whenQrIdNotFound_thenThrowEntityNotFound() throws Exception {
		int id = 1;
		mockMvc.perform(delete("/api/v1/qr/" + id)
								.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.message")
								.value("QR-code with id = " + id + " is not found."));
	}

	@Test
	@DisplayName("qrId найден -> qr удаляется успешно")
	void deleteClubMemberQR_whenQrIdFound_thenQrDeletedSuccessfully() throws Exception {
		Long memberId = 1L;
		QRCode qrCode = qrCodeRepository.save(
					new QRCode(null, memberId, UUID.randomUUID(), false));

		mockMvc.perform(delete("/api/v1/qr/" + qrCode.getId())
								.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

		List<QRCode> qrCodes = qrCodeRepository.findAll();

		assertThat(qrCodes).isEmpty();
	}

	@Test
	@DisplayName("qrId null -> исключение 'внутренняя ошибка сервера'")
	void updateQRCode_whenQrIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(patch("/api/v1/qr/")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"isActive\": false}"))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.message").value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("qrId не найден -> new EntityNotFoundException('QR-code is not found.')")
	void updateQRCode_whenQrIdNotFound_thenThrowEntityNotFound() throws Exception {
		int id = 1;
		mockMvc.perform(patch("/api/v1/qr/" + id)
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"isActive\": false}"))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.message")
								.value("QR-code with id = " + id + " is not found."));
	}

	@Test
	@DisplayName("qrId найден, json null -> срабатывает валидация 'Value can not be null')")
	void updateQRCode_whenQrIdFoundAndJsonNull_thenValidationBeingPerformed() throws Exception {
		Long memberId = 1L;
		QRCode qrCode = qrCodeRepository.save(
					new QRCode(null, memberId, UUID.randomUUID(), false));

		mockMvc.perform(patch("/api/v1/qr/" + qrCode.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content("{}"))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.message").value("Value can not be null"));
	}

	@Test
	@DisplayName("qrId найден, json не null -> устанавливается значение isActive из json")
	void updateQRCode_whenQrIdFoundAndJsonNotNull_thenIsActiveValueSetFromJson() throws Exception {
		Long memberId = 1L;
		QRCode qrCode = qrCodeRepository.save(
					new QRCode(null, memberId, UUID.randomUUID(), true));

		mockMvc.perform(patch("/api/v1/qr/"+qrCode.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"isActive\": false}"))
					.andExpect(status().isOk());

		List<QRCode> qrCodes = qrCodeRepository.findAll();

		assertThat(qrCodes).hasSize(1);
		assertThat(qrCodes.getFirst().getIsActive()).isFalse();
	}
}
