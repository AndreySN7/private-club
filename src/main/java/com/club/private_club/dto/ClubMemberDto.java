package com.club.private_club.dto;

import jakarta.validation.constraints.NotBlank;

public record ClubMemberDto(
			@NotBlank(message = "Value can not be empty")
			String firstName,
			String lastName
) {
}
