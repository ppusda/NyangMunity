package cat.community.NyangMunity.service;

import cat.community.NyangMunity.controller.form.BoardForm;
import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.domain.BoardEditor;
import cat.community.NyangMunity.repository.BoardRepository;
import cat.community.NyangMunity.request.BoardEdit;
import cat.community.NyangMunity.request.BoardSearch;
import cat.community.NyangMunity.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<BoardResponse> getList(BoardSearch boardSearch) {
        return boardRepository.getList(boardSearch).stream()
                .map(BoardResponse::new)
                .collect(Collectors.toList());
    }
    // pagable 객체 이용
    // 위처럼 설정 시 5개를 자동으로 얻어와준다

    @Transactional
    public void edit(Long id, BoardEdit boardEdit) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));

        BoardEditor.BoardEditorBuilder boardEditorBuilder = board.toEditor();

        BoardEditor boardEditor = boardEditorBuilder
                .title(boardEdit.getTitle())
                .content(boardEdit.getContent())
                .build();

        board.edit(boardEditor);

//        if(boardEdit.getTitle() != null){
//            boardEditorBuilder.title(boardEdit.getTitle())
//        }
//
//        if(boardEdit.getContent() != null){
//            boardEditorBuilder.content(boardEdit.getContent())
//        }
//
//        board.edit(boardEditorBuilder.build());

    }
}
