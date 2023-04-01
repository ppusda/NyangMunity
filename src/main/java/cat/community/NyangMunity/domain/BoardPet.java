package cat.community.NyangMunity.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity @Table(name = "BOARD_PET")
@IdClass(BoardPetId.class)
@Getter @Setter
public class BoardPet {

    @Id @ManyToOne @JoinColumn(name = "BOARD_ID")
    private Board board;

    @Id @ManyToOne @JoinColumn(name = "PET_ID")
    private Pet pet;

}
