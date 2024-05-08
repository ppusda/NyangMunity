package cat.community.NyangMunity.board.service;

import cat.community.NyangMunity.board.editor.BoardEditor;
import cat.community.NyangMunity.board.entity.Board;
import cat.community.NyangMunity.board.entity.BoardImage;
import cat.community.NyangMunity.board.entity.BoardLike;
import cat.community.NyangMunity.board.entity.QBoardLike;
import cat.community.NyangMunity.board.response.BoardDetailResponse;
import cat.community.NyangMunity.global.exception.EmptyMaxLikedBoardException;
import cat.community.NyangMunity.global.exception.Unauthorized;
import cat.community.NyangMunity.board.repository.BoardImageRepository;
import cat.community.NyangMunity.board.repository.BoardLikeRepository;
import cat.community.NyangMunity.user.repository.UserRepository;
import cat.community.NyangMunity.board.request.BoardFormRequest;
import cat.community.NyangMunity.global.exception.BoardNotFoundException;
import cat.community.NyangMunity.board.repository.BoardRepository;
import cat.community.NyangMunity.board.request.BoardEditRequest;
import cat.community.NyangMunity.board.response.BoardImageResponse;
import cat.community.NyangMunity.board.response.BoardResponse;
import cat.community.NyangMunity.board.response.LikeBoardResponse;
import cat.community.NyangMunity.user.entity.User;
import com.querydsl.core.Tuple;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardImageRepository boardImageRepository;

    public Board getBoard(Long bid) {
        return boardRepository.findById(bid).orElseThrow(BoardNotFoundException::new);
    }

    public void write(BoardFormRequest boardFormRequest, ArrayList<BoardImage> boardImages, User user){
        Board board = Board.builder()
                .title(boardFormRequest.title())
                .content(boardFormRequest.content())
                .user(user)
                .boardImages(boardImages)
                .createDate(LocalDateTime.now())
                .build();

        List<BoardImage> newBoardImages = new ArrayList<>(boardImages);
        for (BoardImage boardImage: newBoardImages) {
            board.setBoardImages(boardImage);
        }

        boardRepository.save(board);
    }

    public BoardDetailResponse read(Long bid, Long uid) {
        Board board = getBoard(bid);

        return BoardDetailResponse.builder()
                .boardResponse(BoardResponse.from(board, convertToBoardImageResponse(board)))
                .isWriter(isWriter(board.getUser().getId(), uid))
                .build();
    }

    public Page<BoardResponse> getList(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boards = boardRepository.findAllByOrderByCreateDateDesc(pageable);

        return convertToBoardResponse(boards);
    }

    @Transactional
    public void edit(Long bid, BoardEditRequest boardEditRequest, ArrayList<BoardImage> boardImages, Long uid) {
        Board board = getBoard(bid);

        if (!board.getUser().getId().equals(uid)) {
            throw new Unauthorized();
        }

        if(boardEditRequest.removeList() != null && !boardEditRequest.removeList().isEmpty()) {
            for (Long id: boardEditRequest.removeList()) {
                boardImageRepository.deleteById(id);
            }
        }

        for (BoardImage boardImage: boardImages) {
            board.setBoardImages(boardImage);
        }
        boardRepository.save(board);

        BoardEditor.BoardEditorBuilder boardEditorBuilder = board.toEditor();
        BoardEditor boardEditor = boardEditorBuilder
                .title(boardEditRequest.title())
                .content(boardEditRequest.content())
                .build();

        board.edit(boardEditor);
    }

    @Transactional
    public void delete(Long bid, Long uid) {
        Board board = getBoard(bid);

        if (!board.getUser().getId().equals(uid)) {
            throw new Unauthorized();
        }

        boardRepository.delete(board);
    }

    public void like(Long bid, User user) {
        if(likeCheck(bid, user.getId())) {
            boardLikeRepository.deleteByBoardIdAndUserId(bid, user.getId());
            return;
        }

        Board board = getBoard(bid);

        BoardLike boardLike = BoardLike.builder()
                .board(board)
                .user(user)
                .build();

        boardLikeRepository.save(boardLike);
    }

    public boolean likeCheck(Long bid, Long uid) {
        return boardLikeRepository.findByBoardIdAndUserId(bid, uid).isPresent();
    }

    public LikeBoardResponse maxLikeBoard() {
        Board maxLikeBoard = boardLikeRepository.getMaxLikeBoard();

        return LikeBoardResponse.builder()
                .bid(maxLikeBoard.getId())
                .boardImages(convertToBoardImageResponse(maxLikeBoard))
                .nickName(maxLikeBoard.getUser().getNickname())
                .build();
    }

    private Page<BoardResponse> convertToBoardResponse(Page<Board> boardPage) {
        return boardPage.map(board -> BoardResponse.from(board, convertToBoardImageResponse(board)));
    }

    private List<BoardImageResponse> convertToBoardImageResponse(Board board) {
        return board.getBoardImages().stream()
                .map(BoardImageResponse::new)
                .collect(Collectors.toList());
    }

    private boolean isWriter(Long writerId, Long userId) {
        return Objects.equals(writerId, userId);
    }

}
