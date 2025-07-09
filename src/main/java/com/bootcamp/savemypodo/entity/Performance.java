package com.bootcamp.savemypodo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;

@Getter
@Entity
@Table(name = "performances")
public class Performance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long pid;
	private String title;
	private String summary ;
	private LocalDateTime date;
	private int price;
	
	@Transient
	private int reservedSeats;
	
	@Column(name = "seat_number")
	private int seatNumber;
}