package com.club.private_club.controller;

import com.club.private_club.dto.ClubMemberDto;
import com.club.private_club.service.ClubMemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/club_member")
@AllArgsConstructor
public class ClubMemberController {
	private final ClubMemberService clubMemberService;

	@GetMapping(path = {"{uuid}"})
	public ClubMemberDto getClubMember(@PathVariable UUID uuid) {
		return clubMemberService.findByClubMember(uuid);
	}

	@PostMapping(path = "add_member")
	public ResponseEntity<String> addClubMember(@RequestBody ClubMemberDto clubMemberDto) {
		clubMemberService.addClubMember(clubMemberDto);
		return ResponseEntity.status(HttpStatus.CREATED).body("Участник клуба добавлен");
	}

	@PutMapping("update_member/{id}")
	public ResponseEntity<String> updateClubMember(@PathVariable Long id, @RequestBody ClubMemberDto clubMemberDto) {
		return ResponseEntity.status(HttpStatus.OK).body(clubMemberService.updateClubMember(id, clubMemberDto));
	}

	@DeleteMapping("delete_member/{id}")
	public ResponseEntity<String> deleteClubMember(@PathVariable Long id) {
		clubMemberService.deleteClubMember(id);
		return ResponseEntity.status(HttpStatus.OK).body("Участник клуба c id \'" + id + "\' удален");
	}
//	todo: нужно предусмотреть добавление, изменение и удаление участников и QR-кодов через REST-контроллеры.
//	 +1. добавить участника клуба
//	 +2. изменить участника клуба
//	 +3. удалить участника клуба
//	 4. добавить qr-код участнику
//	 5. удалить qr-код у участника
//	 6. изменить qr-код (имеется в виду изменить статус активности?!)
}
