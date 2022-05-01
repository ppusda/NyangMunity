package cat.community.NyangMunity.domain;

import javax.persistence.*;

@Entity @Table(name = "PET_RANK")
public class PetRank {

    @Id @Column(name = "RANK_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "PET_BOARD_ID")
    private PetBoard petBoard;

}
