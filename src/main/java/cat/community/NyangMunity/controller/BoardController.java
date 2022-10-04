package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.controller.form.BoardForm;
import cat.community.NyangMunity.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/write")
    public @ResponseBody BoardForm BoardWrite(@RequestBody @Valid BoardForm boardForm) {
        boardService.write(boardForm);
        return boardForm;
    }
}
