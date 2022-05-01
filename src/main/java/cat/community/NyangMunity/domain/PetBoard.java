package cat.community.NyangMunity.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity @Table(name = "PET_BOARD")
@Getter @Setter
public class PetBoard {

    @Id @Column(name = "PET_BOARD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "PET_ID")
    private Pet pet;

    @ManyToOne @JoinColumn(name = "BOARD_ID")
    private Board board;
}
