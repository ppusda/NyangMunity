package cat.community.nyangmunity.post.repository;

import cat.community.nyangmunity.post.entity.PostLike;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long>, PostLikeRepositoryCustom {

    Optional<PostLike> findByPostIdAndMemberId(Long bid, Long uid);

    @Transactional
    void deleteByPostIdAndMemberId(Long bid, Long uid);

}
