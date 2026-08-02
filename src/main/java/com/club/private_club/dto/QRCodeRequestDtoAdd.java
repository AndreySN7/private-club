package com.club.private_club.dto;

public record QRCodeRequestDtoAdd(
			Integer count
) {
	public QRCodeRequestDtoAdd{
		if (count == null) {
			count = 3;
		}
	}
}
