package cat.community.NyangMunity.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity @Table
@IdClass(BoardPetId.class)
@Getter @Setter
public class BoardPet {

    @Id @ManyToOne @JoinColumn(name = "board_id", columnDefinition = "BIGINT")
    private Board board;

    @Id @ManyToOne @JoinColumn(name = "pet_id", columnDefinition = "BIGINT")
    private Pet pet;

}
