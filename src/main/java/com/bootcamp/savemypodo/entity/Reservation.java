package com.bootcamp.savemypodo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Entity
@Table(name = "reservations")
public class Reservation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "uid", referencedColumnName = "id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "mid", referencedColumnName = "id")
	private Musical musical ;
	
	@ManyToOne
	@JoinColumn(name = "sid")
	private Seat seat ;
}