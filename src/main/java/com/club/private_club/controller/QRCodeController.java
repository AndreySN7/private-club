package com.club.private_club.controller;

import com.club.private_club.dto.QRCodeRequestDtoAdd;
import com.club.private_club.dto.QRCodeRequestDtoPatchQRActive;
import com.club.private_club.service.ClubMemberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/qr")
@AllArgsConstructor
public class QRCodeController {
	private final ClubMemberService clubMemberService;

	@PostMapping("/{id}")
	public ResponseEntity<Void> addClubMemberQR(@PathVariable Long id,
	                                            @RequestBody QRCodeRequestDtoAdd qrCodeRequestDtoAdd) {
		clubMemberService.addClubMemberQR(id, qrCodeRequestDtoAdd);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteClubMemberQR(@PathVariable Long id) {
		clubMemberService.deleteClubMemberQR(id);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{id}")
	public ResponseEntity<String> updateClubMemberQR(@PathVariable Long id,
	                                                 @Valid @RequestBody QRCodeRequestDtoPatchQRActive patchQRActiveDto) {
		clubMemberService.updateQRCode(id, patchQRActiveDto);
		return ResponseEntity.ok().build();
	}
}