package cat.community.nyangmunity.member.service;

import cat.community.nyangmunity.member.provider.GoogleAuthProvider;
import cat.community.nyangmunity.member.response.GoogleUserResponse;

import org.springframework.stereotype.Service;

import cat.community.nyangmunity.member.provider.KakaoAuthProvider;
import cat.community.nyangmunity.member.response.kakao.KakaoUserResponse;
import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final KakaoAuthProvider kakaoAuthProvider;
	private final GoogleAuthProvider googleAuthProvider;

	public String requestKakaoAuthorizationUrl() {
		return kakaoAuthProvider.requestAuthorizationUrl();
	}

	public String requestGoogleAuthorizationUrl() {
		return googleAuthProvider.requestAuthorizationUrl();
	}

	public Mono<KakaoUserResponse> loginWithKakao(String code) {
		return kakaoAuthProvider.getToken(code)
			.flatMap(tokenResponse -> kakaoAuthProvider.getUserInfo(tokenResponse.accessToken()));
	}

	public Mono<GoogleUserResponse> loginWithGoogle(String code) {
		return googleAuthProvider.getToken(code)
			.flatMap(tokenResponse -> googleAuthProvider.getUserInfo(tokenResponse.accessToken()));
	}
}
