package cat.community.NyangMunity.meme.controller;

import cat.community.NyangMunity.meme.response.MemeResponse;
import cat.community.NyangMunity.meme.service.MemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemeController {

    private final MemeService memeService;

    @GetMapping("/meme")
    public Page<MemeResponse> getMemes(@RequestParam("page") int page) {
        return memeService.getMemeList(page);
    }

}
