package cat.community.nyangmunity.image.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import cat.community.nyangmunity.image.entity.Image;
import cat.community.nyangmunity.image.entity.Provider;

public interface ImageRepository extends JpaRepository<Image, String> {

	Page<Image> findAllByProvider(Pageable pageable, Provider provider);

	List<Image> findAllByIdIn(List<String> ids);

}
