package com.bootcamp.savemypodo.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "musicals")
public class Musical {
	
	public static final int TOTAL_SEATS = 140;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	private String title;
	
	@Column(name = "poster_url")
	private String posterUrl ;

	@Column(name = "start_time")
	private LocalTime startTime;
	
	@Column(name = "end_time")
	private LocalTime endTime;
	private String description ;
	private LocalDate date;
	private Long price;
	private String location;
	private Long duration;
	
	@Column(name = "reserved_count")
	private Long reservedCount;
	
	
}