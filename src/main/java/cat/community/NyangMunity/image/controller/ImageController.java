package cat.community.NyangMunity.image.controller;

import cat.community.NyangMunity.image.request.UploadUrlRequest;
import cat.community.NyangMunity.image.response.UploadImageResponse;
import cat.community.NyangMunity.image.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController("/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/upload")
    public UploadImageResponse getUploadURL(@ModelAttribute @Valid UploadUrlRequest uploadUrlRequest) {
        return imageService.createImageInfo(uploadUrlRequest.filename());
    }

    // TODO: 업로드 시 이미지 url과 함께 DB에 기록 / 이후 게시글에 등록되면 연관관계 매핑 진행?
    // 그럼 만약에 tenor를 사용한다면? => 게시글과만 매핑 Image 관리에는 이용 X
    // CreateDate를 적용하여 일정시간이 지난 후 (Batch) 매핑된 게시글이 없을 경우 자동 제거
}
