package cat.community.nyangmunity.image.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import cat.community.nyangmunity.image.entity.Image;
import cat.community.nyangmunity.image.entity.Provider;

public interface ImageRepository extends JpaRepository<Image, Long> {

	Page<Image> findAllByProvider(Pageable pageable, Provider provider);

}
