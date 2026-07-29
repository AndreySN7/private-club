package com.club.private_club.mapper;

import com.club.private_club.dto.ClubMemberDto;
import com.club.private_club.entity.ClubMember;

public class ClubMemberMapper {

	public static ClubMemberDto toDto(ClubMember clubMember) {
		return new ClubMemberDto(
					clubMember.getId(),
					clubMember.getFirstName(),
					clubMember.getLastName()
		);
	}

	public static ClubMember toEntity(ClubMemberDto clubMemberDto) {
		return ClubMember.builder()
					.id(clubMemberDto.id())
					.firstName(clubMemberDto.firstName())
					.lastName(clubMemberDto.lastName())
					.build();
	}
}
