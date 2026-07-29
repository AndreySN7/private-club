package com.club.private_club.service;

import com.club.private_club.dto.ClubMemberDto;

import java.util.UUID;

public interface ClubMemberService {
	ClubMemberDto findByClubMember(UUID uuid);
}
