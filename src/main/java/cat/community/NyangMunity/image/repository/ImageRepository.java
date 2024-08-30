package cat.community.NyangMunity.image.repository;

import cat.community.NyangMunity.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
