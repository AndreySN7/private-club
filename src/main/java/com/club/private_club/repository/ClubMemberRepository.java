package com.club.private_club.repository;

import com.club.private_club.entity.ClubMember;
import com.club.private_club.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
	@Query(value = """
				select cm.*
				from club_member cm join qr_code qr on cm.id = qr.member_id
				where qr.id = :id and is_active = true
				""", nativeQuery = true)
	Optional<ClubMember> findByClubMember(@Param("id") Long id);

	default ClubMember findActiveMemberOrThrow(Long qrCodeId) {
		return findByClubMember(qrCodeId)
					.orElseThrow(() -> new EntityNotFoundException("Entry is not allowed."));
	}
}