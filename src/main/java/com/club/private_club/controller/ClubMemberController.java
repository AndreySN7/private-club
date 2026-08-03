package com.club.private_club.controller;

import com.club.private_club.dto.ClubMemberDto;
import com.club.private_club.service.ClubMemberService;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping(path = "/api/v1/club_member")
@AllArgsConstructor
public class ClubMemberController {
	private final ClubMemberService clubMemberService;

	@GetMapping(path = {"/{id}"})
	public ClubMemberDto getClubMember(@PathVariable(name = "id") Long qrCodeId) {
		return clubMemberService.findByClubMember(qrCodeId);
	}

	@PostMapping()
	public ResponseEntity<Void> addClubMember(@Valid @RequestBody ClubMemberDto clubMemberDto) {
		clubMemberService.addClubMember(clubMemberDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateClubMember(@PathVariable Long id, @Valid @RequestBody ClubMemberDto clubMemberDto) {
		clubMemberService.updateClubMember(id, clubMemberDto);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteClubMember(@PathVariable Long id) {
		clubMemberService.deleteClubMember(id);
		return ResponseEntity.ok().build();
	}
}