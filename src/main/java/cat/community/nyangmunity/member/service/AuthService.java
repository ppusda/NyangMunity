package cat.community.nyangmunity.member.service;

import org.springframework.stereotype.Service;

import cat.community.nyangmunity.member.provider.KakaoAuthProvider;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final KakaoAuthProvider kakaoAuthProvider;

	public String requestAuthorizationUrl() {
		return kakaoAuthProvider.requestAuthorizationUrl();
	}

}
