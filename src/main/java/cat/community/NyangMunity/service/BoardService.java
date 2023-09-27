package cat.community.NyangMunity.service;

import cat.community.NyangMunity.repository.UserRepository;
import cat.community.NyangMunity.request.BoardForm;
import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.domain.BoardEditor;
import cat.community.NyangMunity.domain.BoardImage;
import cat.community.NyangMunity.exception.PostNotFound;
import cat.community.NyangMunity.repository.BoardRepository;
import cat.community.NyangMunity.request.BoardEdit;
import cat.community.NyangMunity.request.BoardSearch;
import cat.community.NyangMunity.response.BoardImageResponse;
import cat.community.NyangMunity.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public void write(BoardForm boardForm, ArrayList<BoardImage> boardImages, Long uid){
        Board board = Board.builder()
                .title(boardForm.getTitle())
                .content(boardForm.getContent())
                .user(userRepository.findById(uid).get())
                .boardImages(boardImages)
                .createDate(LocalDateTime.now())
                .build();

        List<BoardImage> newBoardImages = new ArrayList<>(boardImages);
        for (BoardImage boardImage: newBoardImages) {
            board.setBoardImages(boardImage);
        }

        boardRepository.save(board);
    }

    public BoardResponse read(Long id) {
        // Optional<Board> board = boardRepository.findById(id); // Optional로 Null 체크해도 됨
        Board board = boardRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        List<BoardImageResponse> boardImages = board.getBoardImages().stream()
                                                    .map(BoardImageResponse::new)
                                                    .collect(Collectors.toList());

        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .boardImages(boardImages)
                .createDate(board.getCreateDate())
                .writer(board.getUser())
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
                .orElseThrow(PostNotFound::new);

        BoardEditor.BoardEditorBuilder boardEditorBuilder = board.toEditor();

        BoardEditor boardEditor = boardEditorBuilder
                .title(boardEdit.getTitle())
                .content(boardEdit.getContent())
                .build();

        board.edit(boardEditor);
        // editor를 이용한 방식 (어렵다면 기존 방식을 사용해도 됨. 그냥 setter 처럼 이용)
    }

    @Transactional
    public void delete(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        boardRepository.delete(board);
    }
}
