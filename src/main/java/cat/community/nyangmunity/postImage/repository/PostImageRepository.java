package cat.community.nyangmunity.postImage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cat.community.nyangmunity.postImage.entity.PostImage;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}
