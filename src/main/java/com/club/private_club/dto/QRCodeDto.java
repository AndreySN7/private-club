package com.club.private_club.dto;

import java.util.UUID;

public record QRCodeDto(
			Long id,
			Long memberId,
			UUID oneTimeCode,
			boolean isActive
) {
}
