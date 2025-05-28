package cat.community.nyangmunity.postImage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cat.community.nyangmunity.postImage.entity.PostImageLike;
import jakarta.transaction.Transactional;

@Repository
public interface PostImageLikeRepository
	extends JpaRepository<PostImageLike, Long>, PostImageLikeRepositoryCustom {

	Optional<PostImageLike> findByPostImageIdAndMemberId(Long postImageId, Long memberId);

	@Transactional
	void deleteByPostImageIdAndMemberId(Long postImageId, Long memberId);

}
