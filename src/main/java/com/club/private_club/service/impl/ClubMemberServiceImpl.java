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

	@Override
	@Transactional
	public ClubMemberDto findByClubMember(UUID uuid) {
		ClubMember currentClubMember = clubMemberRepository.findByClubMember(uuid)
					.orElseThrow(() -> new EntityNotFoundException("Entry is not allowed."));
		log.info("ClubMember is found: {}", currentClubMember);

		// Изменяем статус qr-кода на неактивный
		Optional<QRCode> qrCodeOptional = qrCodeRepository.findByMemberIdAndOneTimeCode(currentClubMember.getId(), uuid);
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

	@Override
	public String addClubMember(ClubMemberDto clubMemberDto) {
		ClubMember newClubMember = ClubMemberMapper.toEntity(clubMemberDto);
		clubMemberRepository.save(newClubMember);
		log.info("ClubMember is added: {}", newClubMember);

		return "Club member added";
	}

	@Override
	@Transactional
	public String updateClubMember(Long id, ClubMemberDto clubMemberDto) {
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

		return "Club member with id = " + id + " updated";
	}

	@Override
	public String deleteClubMember(Long id) {
		ClubMember currentClubMember = clubMemberRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Club member with id = " + id + " is not found."));

		clubMemberRepository.deleteById(id);
		log.info("clubMember is removed: {}", currentClubMember);

		return "Club member with id = " + id + " removed";
	}

	@Override
	@Transactional
	public String addClubMemberQR(Long id, QRCodeRequestDtoAdd qrCodeRequest) {
		clubMemberRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Club member with id = " + id + " is not found."));

		Integer count = qrCodeRequest.count();
		count = count > 10 ? 10 : count;


		QRCode newQRCode;

		for (int i = 1; i <= count; i++) {
			newQRCode = QRCode.builder()
						.memberId(id)
						.oneTimeCode(UUID.randomUUID())
						.isActive(true)
						.build();
			qrCodeRepository.save(newQRCode);
			log.info("QR-code is added: {}", newQRCode);
		}

		return "Member with id = " + id + " added " + count + " qr";
	}

	@Override
	public String deleteClubMemberQR(Long id) {
		QRCode currentQRCod = qrCodeRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("QR-code with id = " + id + " is not found."));

		qrCodeRepository.deleteById(id);
		log.info("qrCode is removed: {}", currentQRCod);

		return "QR-code with id = " + id + " removed";
	}

	@Override
	public String updateQRCode(Long id, QRCodeRequestDtoPatchQRActive patchQRActiveDto) {
		QRCode currentQRCode = qrCodeRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("QR-code with id = " + id + " is not found."));

		if (patchQRActiveDto.isActive() != null) {
			currentQRCode.setActive(patchQRActiveDto.isActive());
			qrCodeRepository.save(currentQRCode);
		}
		log.info("qrCode status is updated: {}", currentQRCode);

		return "QR-code with id = " + id + " updated";
	}
}