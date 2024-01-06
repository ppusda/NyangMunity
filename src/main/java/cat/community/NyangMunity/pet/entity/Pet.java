package cat.community.NyangMunity.pet.entity;

import cat.community.NyangMunity.user.entity.User;

import javax.persistence.*;
import java.time.LocalDate;

@Entity @Table(name = "PET")
public class Pet {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private String gender;

    private LocalDate birthday;

}
