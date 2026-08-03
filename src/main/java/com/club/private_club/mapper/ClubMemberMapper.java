package com.club.private_club.mapper;

import com.club.private_club.dto.ClubMemberDto;
import com.club.private_club.entity.ClubMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClubMemberMapper {

	ClubMemberDto toDto(ClubMember clubMember);

	@Mapping(target = "id", ignore = true)
	ClubMember toEntity(ClubMemberDto clubMemberDto);
}
