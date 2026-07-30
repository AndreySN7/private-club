package com.club.private_club.repository;

import com.club.private_club.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
	@Query(value = """
				select * from club_member
				where id in (select member_id from qr_code
						         where one_time_code = :uuid and is_active = true)
				""", nativeQuery = true)
	Optional<ClubMember> findByClubMember(@Param("uuid") UUID uuid);
	}