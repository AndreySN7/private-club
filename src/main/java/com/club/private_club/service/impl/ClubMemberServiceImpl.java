package com.club.private_club.service.impl;

import com.club.private_club.dto.ClubMemberDto;
import com.club.private_club.entity.ClubMember;
import com.club.private_club.entity.QRCode;
import com.club.private_club.exception.EntityNotFoundException;
import com.club.private_club.mapper.ClubMemberMapper;
import com.club.private_club.repository.ClubMemberRepository;
import com.club.private_club.repository.QRCodeRepository;
import com.club.private_club.service.ClubMemberService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ClubMemberServiceImpl implements ClubMemberService {
	private final ClubMemberRepository clubMemberRepository;
	private final QRCodeRepository qrCodeRepository;

	@Override
	@Transactional
	public ClubMemberDto findByClubMember(UUID uuid) {
		Optional<ClubMember> clubMemberOptional = clubMemberRepository.findByClubMember(uuid);
		if (clubMemberOptional.isEmpty()) {
			log.warn("Entry is not allowed.");
			throw new EntityNotFoundException("Entry is not allowed.");
		}
		ClubMember currentClubMember = clubMemberOptional.get();
		log.info("ClubMember is found: {}", currentClubMember);

		// Изменяем статус qr-кода на неактивный
		Optional<QRCode> qrCodeOptional =
					qrCodeRepository.findByMemberIdAndOneTimeCode(currentClubMember.getId(), uuid);
		if (qrCodeOptional.isPresent()) {
			QRCode qrCode = qrCodeOptional.get();
			qrCode.setActive(false);
			qrCodeRepository.save(qrCode);
			log.info("uuid is updated: {}", qrCode);
		}

		// Добавляем клиенту новый qr-код
		QRCode newQRCode = QRCode.builder()
					.memberId(currentClubMember.getId())
					.oneTimeCode(UUID.randomUUID())
					.isActive(true)
					.build();
		qrCodeRepository.save(newQRCode);
		log.info("new qrCode is added: {}", newQRCode);

		return ClubMemberMapper.toDto(currentClubMember);
	}
}
