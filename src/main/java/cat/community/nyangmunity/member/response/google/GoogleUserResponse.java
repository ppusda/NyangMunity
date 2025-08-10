package cat.community.nyangmunity.member.response.google;

public record GoogleUserResponse(
	String id,
	String email,
	String name,
	String picture
) {
}
