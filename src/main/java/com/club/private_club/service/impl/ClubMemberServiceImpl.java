package com.club.private_club.service.impl;

import com.club.private_club.dto.ClubMemberDto;
import com.club.private_club.dto.QRCodeRequestDtoAdd;
import com.club.private_club.dto.QRCodeRequestDtoPatchQRActive;
import com.club.private_club.entity.ClubMember;
import com.club.private_club.entity.QRCode;
import com.club.private_club.exception.DataValidateException;
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
	private final ClubMemberMapper clubMemberMapper;

	@Override
	@Transactional
	public ClubMemberDto findByClubMember(Long qrCodeId) {
		ClubMember currentClubMember = clubMemberRepository.findActiveMemberOrThrow(qrCodeId);
		log.info("ClubMember is found: {}", currentClubMember);

		// Изменяем статус qr-кода на неактивный
		Optional<QRCode> qrCodeOptional = qrCodeRepository.findById(qrCodeId);
		if (qrCodeOptional.isPresent()) {
			QRCode qrCode = qrCodeOptional.get();
			log.info("QRCode: {} ", qrCode);
			qrCode.setIsActive(false);
			qrCodeRepository.save(qrCode);
			log.info("uuid is updated: {}", qrCode);
		}

		// Добавляем клиенту новый qr-код
		QRCode newQRCode = QRCode.builder()
					.memberId(currentClubMember.getId())
					.qr(UUID.randomUUID())
					.isActive(true)
					.build();
		qrCodeRepository.save(newQRCode);
		log.info("new qrCode is added: {}", newQRCode);

		return clubMemberMapper.toDto(currentClubMember);
	}

	@Override
	public void addClubMember(ClubMemberDto clubMemberDto) {
		ClubMember newClubMember = clubMemberMapper.toEntity(clubMemberDto);
		clubMemberRepository.save(newClubMember);
		log.info("ClubMember is added: {}", newClubMember);
	}

	@Override
	@Transactional
	public void updateClubMember(Long id, ClubMemberDto clubMemberDto) {
		if (clubMemberDto.firstName().isBlank()) {
			throw new DataValidateException("The firstName should not be empty.");
		}

		clubMemberRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Club member with id = " + id + " is not found."));


		ClubMember currentClubMember = ClubMember.builder()
					.id(id)
					.firstName(clubMemberDto.firstName())
					.lastName(clubMemberDto.lastName())
					.build();

		clubMemberRepository.save(currentClubMember);
		log.info("clubMember is updated: {}", currentClubMember);
	}

	@Override
	public void deleteClubMember(Long id) {
		ClubMember currentClubMember = clubMemberRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Club member with id = " + id + " is not found."));

		clubMemberRepository.deleteById(id);
		log.info("clubMember is removed: {}", currentClubMember);
	}

	@Override
	@Transactional
	public void addClubMemberQR(Long id, QRCodeRequestDtoAdd qrCodeRequest) {
		clubMemberRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Club member with id = " + id + " is not found."));

		Integer count = qrCodeRequest.count();
		count = count > 10 ? 10 : count;

		QRCode newQRCode;

		for (int i = 0; i < count; i++) {
			newQRCode = QRCode.builder()
						.memberId(id)
						.qr(UUID.randomUUID())
						.isActive(true)
						.build();
			qrCodeRepository.save(newQRCode);
			log.info("QR-code is added: {}", newQRCode);
		}
	}

	@Override
	public void deleteClubMemberQR(Long id) {
		QRCode currentQRCod = qrCodeRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("QR-code with id = " + id + " is not found."));

		qrCodeRepository.deleteById(id);
		log.info("qrCode is removed: {}", currentQRCod);
	}

	@Override
	public void updateQRCode(Long id, QRCodeRequestDtoPatchQRActive patchQRActiveDto) {
		QRCode currentQRCode = qrCodeRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("QR-code with id = " + id + " is not found."));

		currentQRCode.setIsActive(patchQRActiveDto.isActive());
		qrCodeRepository.save(currentQRCode);
		log.info("qrCode status is updated: {}", currentQRCode);
	}
}