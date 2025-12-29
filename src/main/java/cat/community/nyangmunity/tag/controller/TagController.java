package cat.community.nyangmunity.tag.controller;

import cat.community.nyangmunity.tag.entity.Tag;
import cat.community.nyangmunity.tag.response.TagResponse;
import cat.community.nyangmunity.tag.service.TagService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 태그 관련 API를 제공하는 컨트롤러 */
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

  private final TagService tagService;

  /**
   * 인기 태그 목록을 조회합니다.
   *
   * @param limit 조회할 개수 (기본값: 20)
   * @return 인기 태그 목록
   */
  @GetMapping("/popular")
  public ResponseEntity<List<TagResponse>> getPopularTags(
      @RequestParam(defaultValue = "20") int limit) {
    List<Tag> tags = tagService.getPopularTags(limit);
    List<TagResponse> response =
        tags.stream().map(TagResponse::from).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  /**
   * 태그 자동완성 결과를 조회합니다.
   *
   * @param keyword 검색 키워드
   * @return 매칭되는 태그 목록
   */
  @GetMapping("/autocomplete")
  public ResponseEntity<List<TagResponse>> autocomplete(@RequestParam String keyword) {
    List<Tag> tags = tagService.autocomplete(keyword);
    List<TagResponse> response =
        tags.stream().map(TagResponse::from).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  /**
   * 특정 이미지의 태그 목록을 조회합니다.
   *
   * @param imageId 이미지 ID
   * @return 태그 목록
   */
  @GetMapping("/image")
  public ResponseEntity<List<TagResponse>> getImageTags(@RequestParam String imageId) {
    List<Tag> tags = tagService.getImageTags(imageId);
    List<TagResponse> response =
        tags.stream().map(TagResponse::from).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }
}
