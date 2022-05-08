package cat.community.NyangMunity.repository;

import cat.community.NyangMunity.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public void save(User user){
        user.setEmail(user.getEmail());
        user.setPassword(user.getPassword());
        em.persist(user);
    }

    public List<User> findAll(){
        return em.createQuery("select u from User u", User.class)
                .getResultList();
    }

    public User findByEmailPassword(String email, String password){
        return em.createQuery("select u from User u where u.email = :email and u.password =:password", User.class)
                .setParameter("email", email)
                .setParameter("password", password)
                .getSingleResult();

    }

}
