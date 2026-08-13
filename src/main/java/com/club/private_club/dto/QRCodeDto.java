package com.club.private_club.dto;

import java.util.UUID;

public record QRCodeDto(
			Long memberId,
			UUID qr,
			Boolean isActive
) {
}
