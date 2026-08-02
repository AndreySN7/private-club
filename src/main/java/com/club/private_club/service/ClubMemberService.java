package com.club.private_club.service;

import com.club.private_club.dto.ClubMemberDto;
import com.club.private_club.dto.QRCodeRequestDtoAdd;
import com.club.private_club.dto.QRCodeRequestDtoPatchQRActive;

import java.util.UUID;

public interface ClubMemberService {
	ClubMemberDto findByClubMember(UUID uuid);

	String addClubMember(ClubMemberDto clubMemberDto);

	String updateClubMember(Long id, ClubMemberDto clubMemberDto);

	String deleteClubMember(Long id);

	String addClubMemberQR(Long id, QRCodeRequestDtoAdd qrCodeRequestDtoAdd);

	String deleteClubMemberQR(Long id);

	String updateQRCode(Long id, QRCodeRequestDtoPatchQRActive patchQRActiveDto);
}