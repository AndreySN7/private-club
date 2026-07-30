package com.club.private_club.repository;

import com.club.private_club.entity.QRCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface QRCodeRepository extends JpaRepository<QRCode, Long> {

	Optional<QRCode> findByMemberIdAndOneTimeCode(Long memberId, UUID oneTimeCode);
}
