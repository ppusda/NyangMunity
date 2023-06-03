package cat.community.NyangMunity.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity @Table
@IdClass(BoardPetId.class)
@Getter @Setter
public class BoardPet {

    @Id @ManyToOne @JoinColumn(name = "BOARD_ID", columnDefinition = "BIGINT")
    private Board board;

    @Id @ManyToOne @JoinColumn(name = "PET_ID", columnDefinition = "BIGINT")
    private Pet pet;

}
