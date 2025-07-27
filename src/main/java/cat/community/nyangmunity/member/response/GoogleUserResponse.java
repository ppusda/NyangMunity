package cat.community.nyangmunity.member.response;

public record GoogleUserResponse(
    String id,
    String email,
    String name,
    String picture
) {
}
