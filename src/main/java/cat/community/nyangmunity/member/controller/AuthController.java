package cat.community.nyangmunity.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cat.community.nyangmunity.member.service.AuthService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@GetMapping("/kakao")
	public String requestAuthorizationUrl() {
		return authService.requestAuthorizationUrl();
	}

	@GetMapping("/kakaoLogin")
	public String redirectKakaoLogin() {
		return "인가코드 요청 redirect 받기";
	}
}
