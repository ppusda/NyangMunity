package cat.community.NyangMunity.board.entity;

import cat.community.NyangMunity.pet.entity.Pet;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity @Table
@IdClass(BoardPetId.class)
@Getter @Setter
public class BoardPet {

    @Id @ManyToOne
    @JoinColumn(name = "board_id", columnDefinition = "BIGINT")
    private Board board;

    @Id @ManyToOne
    @JoinColumn(name = "pet_id", columnDefinition = "BIGINT")
    private Pet pet;

}
