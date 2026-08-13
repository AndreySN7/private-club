package com.club.private_club.dto;

import jakarta.validation.constraints.NotNull;

public record QRCodeRequestDtoPatchQRActive(
			@NotNull(message = "Value can not be null")
			Boolean isActive
) {}