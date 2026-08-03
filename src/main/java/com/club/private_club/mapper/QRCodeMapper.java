package com.club.private_club.mapper;

import com.club.private_club.dto.QRCodeDto;
import com.club.private_club.entity.QRCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QRCodeMapper {

	QRCodeDto toDto(QRCode qrCode);

	@Mapping(target = "id", ignore = true)
	QRCode toEntity(QRCodeDto qrCodeDto);
}
