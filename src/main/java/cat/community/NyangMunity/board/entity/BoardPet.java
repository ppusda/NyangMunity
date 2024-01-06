package cat.community.NyangMunity.board.entity;

import cat.community.NyangMunity.pet.entity.Pet;
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
