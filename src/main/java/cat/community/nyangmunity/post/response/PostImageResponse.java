package cat.community.nyangmunity.post.response;


import cat.community.nyangmunity.image.entity.Image;
import lombok.Builder;

@Builder
public record PostImageResponse(
        String id,
        String name,
        String url
) {

    public static PostImageResponse from(Image image) {
        return PostImageResponse.builder()
                .id(image.getId())
                .name(image.getName())
                .url(image.getUrl())
                .build();
    }

}
