package com.club.private_club.service;

import com.club.private_club.dto.ClubMemberDto;
import com.club.private_club.dto.QRCodeRequestDtoAdd;
import com.club.private_club.dto.QRCodeRequestDtoPatchQRActive;

public interface ClubMemberService {
	ClubMemberDto findByClubMember(Long qrCodeId);

	void addClubMember(ClubMemberDto clubMemberDto);

	void updateClubMember(Long id, ClubMemberDto clubMemberDto);

	void deleteClubMember(Long id);

	void addClubMemberQR(Long id, QRCodeRequestDtoAdd qrCodeRequestDtoAdd);

	void deleteClubMemberQR(Long id);

	void updateQRCode(Long id, QRCodeRequestDtoPatchQRActive patchQRActiveDto);
}