package cat.community.nyangmunity.pet.entity;

import java.time.LocalDate;

import cat.community.nyangmunity.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Pet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String name;

	@Column
	private LocalDate birthday;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

}
