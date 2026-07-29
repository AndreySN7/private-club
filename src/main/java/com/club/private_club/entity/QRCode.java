package com.club.private_club.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "qr_code")
public class QRCode {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	Long memberId;
	@NonNull
	UUID oneTimeCode;
	boolean isActive;
}
