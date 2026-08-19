package com.club.private_club.controller;

import com.club.private_club.dto.ClubMemberDto;
import com.club.private_club.entity.ClubMember;
import com.club.private_club.entity.QRCode;
import com.club.private_club.repository.ClubMemberRepository;
import com.club.private_club.repository.QRCodeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClubMemberControllerIT extends AbstractIntegrationTest {

	@Autowired
	private ObjectMapper objectMapper;

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
	void getClubMember_whenLetters_thenInternalServerError() throws Exception {
		mockMvc.perform(get("/api/v1/club_member/a"))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.message").value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("пустое имя (firstName) -> выполняется валидация 'Value can not be empty'")
	void addClubMember_whenFirstNameEmpty_thenValidationBeingPerformed() throws Exception {
		ClubMemberDto clubMemberDto = new ClubMemberDto("", "Smith");
		String json = objectMapper.writeValueAsString(clubMemberDto);

		mockMvc.perform(post("/api/v1/club_member")
								.contentType(MediaType.APPLICATION_JSON)
								.content(json))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.message").value("Value can not be empty"));
	}

	@Test
	@DisplayName("firstName непустое, lastName пустое или null -> участник создается успешно")
	void addClubMember_whenFirstNameNotEmptyAndLastNameEmptyOrNull_thenParticipantCreatedSuccessfully() throws Exception {
		ClubMemberDto clubMemberDto = new ClubMemberDto("Bill", "");
		String json = objectMapper.writeValueAsString(clubMemberDto);

		mockMvc.perform(post("/api/v1/club_member")
								.contentType(MediaType.APPLICATION_JSON)
								.content(json))
					.andExpect(status().isCreated());

		List<ClubMember> newClubMember = clubMemberRepository.findAll();

		assertThat(newClubMember).hasSize(1);
		Assertions.assertEquals("Bill", newClubMember.getFirst().getFirstName());
		Assertions.assertEquals("", newClubMember.getLast().getLastName());
	}

	@Test
	@DisplayName("firstName непустое, lastName непустое -> участник создается успешно")
	void addClubMember_whenFirstNameNotEmptyAndLastNameNotEmpty_thenParticipantCreatedSuccessfully() throws Exception {
		ClubMemberDto clubMemberDto = new ClubMemberDto("John", "Holly");
		String json = objectMapper.writeValueAsString(clubMemberDto);

		mockMvc.perform(post("/api/v1/club_member")
								.contentType(MediaType.APPLICATION_JSON)
								.content(json))
					.andExpect(status().isCreated());

		List<ClubMember> newClubMember = clubMemberRepository.findAll();
		assertThat(newClubMember).hasSize(1);
		Assertions.assertEquals("John", newClubMember.getFirst().getFirstName());
		Assertions.assertEquals("Holly", newClubMember.getFirst().getLastName());
	}

	@Test
	@DisplayName("json пустой -> срабатывает валидация 'Value can not be empty'")
	void addClubMember_whenJsonNull_thenValidationBeingPerformed() throws Exception {
		mockMvc.perform(post("/api/v1/club_member")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{}"))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.message").value("Value can not be empty"));
	}

	@Test
	@DisplayName("id null -> исключение 'внутренняя ошибка сервера'")
	void updateClubMember_whenIdNull_thenInternalServerError() throws Exception {
		ClubMemberDto clubMember = new ClubMemberDto("Bob", "Ri");
		String json = objectMapper.writeValueAsString(clubMember);

		mockMvc.perform(put("/api/v1/club_member")
								.contentType(MediaType.APPLICATION_JSON)
								.content(json))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.message").value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("id не найден -> исключение EntityNotFoundException(Club member is not found.)")
	void updateClubMember_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		ClubMemberDto clubMember = new ClubMemberDto("Bob", "Ri");
		String json = objectMapper.writeValueAsString(clubMember);
		int id = 1;

		mockMvc.perform(put("/api/v1/club_member/" + id)
								.contentType(MediaType.APPLICATION_JSON)
								.content(json))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.message")
								.value("Club member with id = " + id + " is not found."));
	}

	@Test
	@DisplayName("id найден, json пустой -> срабатывает валидация 'Value can not be empty'")
	void updateClubMember_whenIdFoundAndJsonNull_thenValidationBeingPerformed() throws Exception {
		ClubMember clubMember = clubMemberRepository.save(
					new ClubMember(null, "Ron", "Ko"));

		mockMvc.perform(put("/api/v1/club_member/" + clubMember.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content("{}"))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.message").value("Value can not be empty"));
	}

	@Test
	@DisplayName("id найден, firstName пустое или null -> срабатывает валидация 'Value can not be empty'")
	void updateClubMember_whenIdFoundAndFirstNameNullOrEmpty_thenValidationBeingPerformed() throws Exception {
		ClubMember clubMember = clubMemberRepository.save(
					new ClubMember(null, "Bob", "Si"));
		ClubMemberDto clubMemberDto = new ClubMemberDto("", "Si");
		String json = objectMapper.writeValueAsString(clubMemberDto);

		mockMvc.perform(put("/api/v1/club_member/" + clubMember.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content(json))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.message").value("Value can not be empty"));
	}

	@Test
	@DisplayName("id найден, firstName непустое, lastName пустое или null -> пользователь обновляется успешно")
	void updateClubMember_whenIdFoundAndFirstNameNotEmptyAndLastNameNullOrEmpty_thenParticipantCreatedSuccessfully() throws Exception {
		ClubMember clubMember = clubMemberRepository.save(
					new ClubMember(null, "Rob", "Jons"));
		ClubMemberDto clubMemberDto = new ClubMemberDto("Larry", null);
		String json = objectMapper.writeValueAsString(clubMemberDto);

		mockMvc.perform(put("/api/v1/club_member/" + clubMember.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content(json))
					.andExpect(status().isOk());

		assertThat(clubMemberRepository.findById(clubMember.getId()))
					.isPresent()
					.get()
					.extracting(ClubMember::getFirstName, ClubMember::getLastName)
					.containsExactly("Larry", null);
	}

	@Test
	@DisplayName("id найден, firstName непустое, lastName непустое -> пользователь обновляется успешно")
	void updateClubMember_whenIdFoundAndFirstNameNotEmptyAndLastNameNotEmpty_thenParticipantCreatedSuccessfully() throws Exception {
		ClubMember clubMember = clubMemberRepository.save(
					new ClubMember(null, "Rob", "Jons"));
		ClubMemberDto clubMemberDto = new ClubMemberDto("Larry", "Bin");
		String json = objectMapper.writeValueAsString(clubMemberDto);

		mockMvc.perform(put("/api/v1/club_member/" + clubMember.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content(json))
					.andExpect(status().isOk());

		assertThat(clubMemberRepository.findById(clubMember.getId()))
					.isPresent()
					.get()
					.extracting(ClubMember::getFirstName, ClubMember::getLastName)
					.containsExactly("Larry", "Bin");
	}

	@Test
	@DisplayName("id null -> исключение 'внутренняя ошибка сервера'")
	void deleteClubMember_whenIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(delete("/api/v1/club_member/")
								.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.message").value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("id не найден -> исключение EntityNotFoundException('Club member is not found.')")
	void deleteClubMember_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		int id = 1;
		mockMvc.perform(delete("/api/v1/club_member/" + id)
								.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.message").value("Club member with id = " + id + " is not found."));
	}

	@Test
	@DisplayName("id найден -> пользователь удален успешно")
	void deleteClubMember_whenIdFound_thenParticipantDeletedSuccessfully() throws Exception {
		ClubMember clubMember = clubMemberRepository.save(
					new ClubMember(null, "Jane", "Pim"));

		mockMvc.perform(delete("/api/v1/club_member/" + clubMember.getId())
								.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

		assertThat(clubMemberRepository.findById(clubMember.getId())).isNotPresent();
	}
}