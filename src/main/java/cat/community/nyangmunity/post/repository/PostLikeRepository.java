package cat.community.nyangmunity.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cat.community.nyangmunity.post.entity.PostImageLike;
import jakarta.transaction.Transactional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostImageLike, Long>, PostLikeRepositoryCustom {

	Optional<PostImageLike> findByPostImageIdAndMemberId(Long postImageId, Long memberId);

	@Transactional
	void deleteByPostImageIdAndMemberId(Long postImageId, Long memberId);

}
