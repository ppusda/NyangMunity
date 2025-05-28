package cat.community.nyangmunity.postImage.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cat.community.nyangmunity.postImage.post.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	Page<Post> findAllByOrderByCreateDateDesc(Pageable pageable);
}
