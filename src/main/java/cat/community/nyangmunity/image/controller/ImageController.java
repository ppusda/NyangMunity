package cat.community.nyangmunity.image.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cat.community.nyangmunity.image.batch.response.ImageResponse;
import cat.community.nyangmunity.image.request.UploadUrlRequest;
import cat.community.nyangmunity.image.response.UploadImageResponse;
import cat.community.nyangmunity.image.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController("/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/meme")
    public Page<ImageResponse> getMemes(@RequestParam("page") int page) {
        return imageService.getImageList(page);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/upload")
    public UploadImageResponse getUploadURL(@ModelAttribute @Valid UploadUrlRequest uploadUrlRequest) {
        return imageService.createImageInfo(uploadUrlRequest.filename());
    }

}
