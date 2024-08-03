package cat.community.NyangMunity.meme.repository;

import cat.community.NyangMunity.meme.entity.Meme;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemeRepository extends JpaRepository<Meme, String> {

    List<Meme> findByIdAndProvider(String id, String provider);
}
