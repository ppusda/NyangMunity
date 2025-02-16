package cat.community.nyangmunity.image.repository;

import java.util.List;

import cat.community.nyangmunity.image.entity.Image;
import cat.community.nyangmunity.image.entity.Provider;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

	List<Image> findByIdAndProvider(String id, Provider provider);

}
