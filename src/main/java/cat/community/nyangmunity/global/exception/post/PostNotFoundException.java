package cat.community.nyangmunity.global.exception.post;

import cat.community.nyangmunity.global.exception.NyangmunityException;
import lombok.Getter;

@Getter
public class PostNotFoundException extends NyangmunityException {

	private static final String MESSAGE = "존재하지 않는 글입니다.";

	public PostNotFoundException() {
		super(MESSAGE);
	}

	public PostNotFoundException(String message) {
		super(message);
	}

	@Override
	public int getStatusCode() {
		return 404;
	}
}
