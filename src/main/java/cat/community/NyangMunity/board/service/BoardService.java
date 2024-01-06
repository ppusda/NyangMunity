package cat.community.NyangMunity.board.service;

import cat.community.NyangMunity.board.editor.BoardEditor;
import cat.community.NyangMunity.board.entity.Board;
import cat.community.NyangMunity.board.entity.BoardImage;
import cat.community.NyangMunity.board.entity.BoardLike;
import cat.community.NyangMunity.domain.*;
import cat.community.NyangMunity.global.exception.EmptyMaxLikedBoardException;
import cat.community.NyangMunity.global.exception.Unauthorized;
import cat.community.NyangMunity.board.repository.BoardImageRepository;
import cat.community.NyangMunity.board.repository.BoardLikeRepository;
import cat.community.NyangMunity.user.repository.UserRepository;
import cat.community.NyangMunity.board.request.BoardForm;
import cat.community.NyangMunity.global.exception.PostNotFound;
import cat.community.NyangMunity.board.repository.BoardRepository;
import cat.community.NyangMunity.board.response.BoardEdit;
import cat.community.NyangMunity.board.request.BoardSearch;
import cat.community.NyangMunity.board.response.BoardImageResponse;
import cat.community.NyangMunity.board.response.BoardResponse;
import cat.community.NyangMunity.board.response.LikeBoardResponse;
import cat.community.NyangMunity.user.entity.User;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardImageRepository boardImageRepository;

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

    public BoardResponse read(Long bid) {
        // Optional<Board> board = boardRepository.findById(id); // Optional로 Null 체크해도 됨
        Board board = boardRepository.findById(bid)
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
                .uid(board.getUser().getId())
                .writer(board.getUser().getNickname())
                .build();
    }

    public Long getCount() {
        return boardRepository.count();
    }

    public List<BoardResponse> getList(BoardSearch boardSearch) {
        return boardRepository.getList(boardSearch).stream()
                .map(BoardResponse::new)
                .collect(Collectors.toList());
    }
    // pagable 객체 이용
    // 위처럼 설정 시 5개를 자동으로 얻어와준다

    @Transactional
    public void edit(Long bid, BoardEdit boardEdit, ArrayList<BoardImage> boardImages, Long uid) {
        Board board = boardRepository.findById(bid)
                .orElseThrow(PostNotFound::new);

        if(board.getUser().getId().equals(uid)) {
            if(boardEdit.getRemoveList() != null && !boardEdit.getRemoveList().isEmpty()) {
                for (Long id: boardEdit.getRemoveList()) {
                    boardImageRepository.deleteById(id);
                }
            }

            for (BoardImage boardImage: boardImages) {
                board.setBoardImages(boardImage);
            }
            boardRepository.save(board);

            BoardEditor.BoardEditorBuilder boardEditorBuilder = board.toEditor();
            BoardEditor boardEditor = boardEditorBuilder
                    .title(boardEdit.getTitle())
                    .content(boardEdit.getContent())
                    .build();
            board.edit(boardEditor); // editor를 이용한 방식 (어렵다면 기존 방식을 사용해도 됨. 그냥 setter 처럼 이용)
        }else {
            log.error(">>> 권한이 없습니다.");
            throw new Unauthorized();
        }
    }

    @Transactional
    public void delete(Long bid, Long uid) {
        Board board = boardRepository.findById(bid)
                .orElseThrow(PostNotFound::new);

        if(board.getUser().getId().equals(uid)) {
            log.error(">>> 권한이 없습니다.");
            boardRepository.delete(board);
        }else {
            throw new Unauthorized();
        }
    }

    public void like(Long bid, Long uid) {
        if(likeCheck(bid, uid)) {
            boardLikeRepository.deleteByBoardIdAndUserId(bid, uid);
        }else {
            Board board = boardRepository.findById(bid)
                    .orElseThrow(PostNotFound::new);

            User user = userRepository.findById(uid)
                    .orElseThrow(PostNotFound::new);

            BoardLike boardLike = BoardLike.builder()
                    .board(board)
                    .user(user)
                    .build();

            boardLikeRepository.save(boardLike);
        }
    }

    public boolean likeCheck(Long bid, Long uid) {
        if(boardLikeRepository.findByBoardIdAndUserId(bid, uid).isPresent()) {
            return true;
        }else {
            return false;
        }
    }

    public LikeBoardResponse maxLikeBoard() {
        List<Tuple> maxBoardLike = boardLikeRepository.getMaxLikeBoard();

        if(!maxBoardLike.isEmpty()) {
            Board board = boardRepository.findById((maxBoardLike.get(0).get(QBoardLike.boardLike.board.id)))
                    .orElseThrow(EmptyMaxLikedBoardException::new);

            List<BoardImageResponse> boardImages = board.getBoardImages().stream()
                    .map(BoardImageResponse::new)
                    .collect(Collectors.toList());

            return LikeBoardResponse.builder()
                    .bid(board.getId())
                    .boardImages(boardImages)
                    .nickName(board.getUser().getNickname())
                    .build();
        }else {
            throw new EmptyMaxLikedBoardException();
        }
    }

}
