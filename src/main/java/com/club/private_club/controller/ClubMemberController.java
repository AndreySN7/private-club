package com.club.private_club.controller;

import com.club.private_club.dto.ClubMemberDto;
import com.club.private_club.dto.QRCodeRequestDtoAdd;
import com.club.private_club.dto.QRCodeRequestDtoPatchQRActive;
import com.club.private_club.service.ClubMemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
		return ResponseEntity.status(HttpStatus.CREATED).body(clubMemberService.addClubMember(clubMemberDto));
	}

	@PutMapping("update_member/{id}")
	public ResponseEntity<String> updateClubMember(@PathVariable Long id, @RequestBody ClubMemberDto clubMemberDto) {
		return ResponseEntity.ok().body(clubMemberService.updateClubMember(id, clubMemberDto));
	}

	@DeleteMapping("delete_member/{id}")
	public ResponseEntity<String> deleteClubMember(@PathVariable Long id) {
		return ResponseEntity.ok().body(clubMemberService.deleteClubMember(id));
	}

	@PostMapping("add_qr/{id}")
	public ResponseEntity<String> addClubMemberQR(@PathVariable Long id,
	                                              @RequestBody QRCodeRequestDtoAdd qrCodeRequestDtoAdd) {
		return ResponseEntity.status(HttpStatus.CREATED).body(clubMemberService.addClubMemberQR(id, qrCodeRequestDtoAdd));
	}

	@DeleteMapping("delete_qr/{id}")
	public ResponseEntity<String> deleteClubMemberQR(@PathVariable Long id) {
		return ResponseEntity.ok().body(clubMemberService.deleteClubMemberQR(id));
	}

	@PatchMapping("update_qr/{id}")
	public ResponseEntity<String> updateClubMemberQR(@PathVariable Long id,
	                                                 @RequestBody QRCodeRequestDtoPatchQRActive patchQRActiveDto) {
		return ResponseEntity.ok().body(clubMemberService.updateQRCode(id, patchQRActiveDto));
	}
}