package cat.community.nyangmunity.member.service;

import org.springframework.stereotype.Service;

import cat.community.nyangmunity.member.provider.KakaoAuthProvider;
import cat.community.nyangmunity.member.response.KakaoUserResponse;
import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final KakaoAuthProvider kakaoAuthProvider;

	public String requestAuthorizationUrl() {
		return kakaoAuthProvider.requestAuthorizationUrl();
	}

	public Mono<KakaoUserResponse> loginWithKakao(String code) {
		return kakaoAuthProvider.getToken(code)
			.flatMap(tokenResponse -> kakaoAuthProvider.getUserInfo(tokenResponse.accessToken()));
	}
}
