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

    // 프론트 단에서 버튼 분리 => 구현도 힘들고 후속 처리도 너무 복잡해짐 => 이건 원래 구상한 바가 사실 이미지를 url로 변환하여 공유하는게 목적이어서 고민이 더 필요해 보임
    // 어떻게 하면 사용자가 쉽게 이미지를 업로드 하고 이용할 수 있을까? //
    // 이미지 업로드 => 이미지 업로드 & url 변환 후 입력창 위쪽으로 글 등록 대기 상태 => 글 등록 (연관관계 편의 메서드로 추가 등록) // 문제 없음
    // Tenor 는? 이미지 클릭 => 입력창 위쪽으로 글 등록 대기 상태 => 글 등록 // Image와 Meme이 호환되지 않음... (Meme을 따로 등록해야할까?)

    // 글 등록 버튼 클릭 시 이미지 업로드 (presigned url 반환, 클라이언트 측에서 별도로 진행), 글 저장 (url, image id로 연관관계 편의 메서드를 통해 저장)

    // 기존에 구상했던 tenor 데이터 업데이트 정책 부터 갈아엎어야할 것 같다. 사실 Meme으로 가져갈게 아니라 이미지 제공 서비스가 더 방향성에 맞아보인다.
    // TODO: Meme -> Image로 통합 작업 필요, Provider 등 이관 및 Tenor 외 이미지 제공처 추가
    // TODO: 이미지 업로드 시 이미지 url과 함께 DB에 기록 (직접 업로드의 경우 url 생성 및 저장, Tenor의 경우 단순히 게시글과 매핑) / 게시글에 등록되면 연관관계 매핑 진행 (연과관계 편의 메서드)
    // TODO: Nyangmunity에서 직접 관리하는 이미지의 경우 createDate를 적용하여 일정시간이 지난 후 (Batch) 매핑된 게시글이 없을 경우 자동 제거
}
