package cat.community.NyangMunity.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private String id;

    private String password;
    private String sex;
    private String birthday;

    private String nickname;

    @OneToMany(mappedBy = "member")
    private List<Board> board= new ArrayList<>();
}
