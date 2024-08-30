package cat.community.NyangMunity.board.service;

import cat.community.NyangMunity.board.editor.BoardEditor;
import cat.community.NyangMunity.board.entity.Board;
import cat.community.NyangMunity.board.entity.BoardLike;
import cat.community.NyangMunity.global.exception.UnauthorizedUserException;
import cat.community.NyangMunity.board.repository.BoardLikeRepository;
import cat.community.NyangMunity.board.request.BoardFormRequest;
import cat.community.NyangMunity.global.exception.BoardNotFoundException;
import cat.community.NyangMunity.board.repository.BoardRepository;
import cat.community.NyangMunity.board.request.BoardEditRequest;
import cat.community.NyangMunity.board.response.BoardImageResponse;
import cat.community.NyangMunity.board.response.BoardResponse;
import cat.community.NyangMunity.board.response.LikeBoardResponse;
import cat.community.NyangMunity.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Board getBoard(Long bid) {
        return boardRepository.findById(bid).orElseThrow(BoardNotFoundException::new);
    }

    public void write(BoardFormRequest boardFormRequest, User user){
        Board board = Board.builder()
                .content(boardFormRequest.content())
                .user(user)
                .createDate(LocalDateTime.now())
                .build();

        boardRepository.save(board);
    }

    public BoardResponse read(Long bid) {
        Board board = getBoard(bid);
        return BoardResponse.from(board, convertToBoardImageResponse(board));
    }

    public Page<BoardResponse> getList(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boards = boardRepository.findAllByOrderByCreateDateDesc(pageable);

        return convertToBoardResponse(boards);
    }

    @Transactional
    public void edit(Long bid, BoardEditRequest boardEditRequest, Long uid) {
        Board board = getBoard(bid);

        if (!board.getUser().getId().equals(uid)) {
            throw new UnauthorizedUserException();
        }

        boardRepository.save(board);

        BoardEditor.BoardEditorBuilder boardEditorBuilder = board.toEditor();
        BoardEditor boardEditor = boardEditorBuilder
                .content(boardEditRequest.content())
                .build();

        board.edit(boardEditor);
    }

    @Transactional
    public void delete(Long bid, Long uid) {
        Board board = getBoard(bid);

        if (!board.getUser().getId().equals(uid)) {
            throw new UnauthorizedUserException();
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
        return board.getImages().stream()
                .map(BoardImageResponse::from)
                .collect(Collectors.toList());
    }

    private boolean isWriter(Long writerId, Long userId) {
        return Objects.equals(writerId, userId);
    }

}
