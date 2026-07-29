package com.club.private_club.service.impl;

import com.club.private_club.dto.ClubMemberDto;
import com.club.private_club.entity.ClubMember;
import com.club.private_club.exception.EntityNotFoundException;
import com.club.private_club.mapper.ClubMemberMapper;
import com.club.private_club.repository.ClubMemberRepository;
import com.club.private_club.repository.QRCodeRepository;
import com.club.private_club.service.ClubMemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ClubMemberServiceImpl implements ClubMemberService {
	private final ClubMemberRepository clubMemberRepository;
	private final QRCodeRepository qrCodeRepository;

	@Override
	public ClubMemberDto findByClubMember(UUID uuid) {
		Optional<ClubMember> clubMember = clubMemberRepository.findByClubMember(uuid);
		if (clubMember.isEmpty()) {
			throw new EntityNotFoundException("Entry is not allowed.");
		}

		return ClubMemberMapper.toDto(clubMember.get());
	}
}
