package com.club.private_club.service;

import com.club.private_club.dto.ClubMemberDto;

import java.util.UUID;

public interface ClubMemberService {
	ClubMemberDto findByClubMember(UUID uuid);

	ClubMemberDto addClubMember(ClubMemberDto clubMemberDto);

	String updateClubMember(Long id, ClubMemberDto clubMemberDto);

	void deleteClubMember(Long id);
}
