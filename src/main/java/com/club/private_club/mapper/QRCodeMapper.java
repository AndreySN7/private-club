package com.club.private_club.mapper;

import com.club.private_club.dto.QRCodeDto;
import com.club.private_club.entity.QRCode;

public class QRCodeMapper {

	public static QRCodeDto toDto (QRCode qrCode) {
		return new QRCodeDto(
					qrCode.getId(),
					qrCode.getMemberId(),
					qrCode.getOneTimeCode(),
					qrCode.isActive()
		);
	}

	public static QRCode toEntity (QRCodeDto qrCodeDto) {
		return QRCode.builder()
					.id(qrCodeDto.id())
					.memberId(qrCodeDto.memberId())
					.oneTimeCode(qrCodeDto.oneTimeCode())
					.isActive(qrCodeDto.isActive())
					.build();
	}
}
