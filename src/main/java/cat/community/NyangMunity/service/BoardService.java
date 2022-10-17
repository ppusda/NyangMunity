package cat.community.NyangMunity.service;

import cat.community.NyangMunity.controller.form.BoardForm;
import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.repository.BoardRepository;
import cat.community.NyangMunity.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public void write(BoardForm boardForm){
        Board board = Board.builder()
                .title(boardForm.getTitle())
                .content(boardForm.getContent())
                .build();

        boardRepository.save(board);
    }

    public BoardResponse read(Long id) {
        // Optional<Board> board = boardRepository.findById(id); // Optional로 Null 체크해도 됨
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));

        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .build();
    }

}
