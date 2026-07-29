package com.club.private_club.controller;

import com.club.private_club.dto.ClubMemberDto;
import com.club.private_club.service.ClubMemberService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/club_member")
@AllArgsConstructor
public class ClubMemberController {
	private final ClubMemberService clubMemberService;

	@GetMapping(path = {"{uuid}"})
	public ClubMemberDto getClubMember(@PathVariable UUID uuid) {
		return clubMemberService.findByClubMember(uuid);
	}
}
