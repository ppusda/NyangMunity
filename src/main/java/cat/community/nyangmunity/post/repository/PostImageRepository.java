package cat.community.nyangmunity.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cat.community.nyangmunity.post.entity.PostImage;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}
