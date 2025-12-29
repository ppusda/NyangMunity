package cat.community.nyangmunity.postImage.image.request;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 단일 이미지 업로드 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class ImageUploadRequest {

  private String imageId;
  private String description;
  private List<String> tags;
}
