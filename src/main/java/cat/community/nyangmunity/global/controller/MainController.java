package cat.community.nyangmunity.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	@GetMapping("/api")
	public String main() {
		return "docs/apiInfo";
	}
}
