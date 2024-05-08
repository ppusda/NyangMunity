package cat.community.NyangMunity.board.response;

import cat.community.NyangMunity.board.entity.BoardImage;
import java.io.IOException;
import lombok.Builder;
import lombok.Getter;

import java.nio.file.Files;
import java.nio.file.Paths;

public record BoardImageResponse(
        Long id,
        String name,
        Long size,
        byte[] imageBytes
) {

    public BoardImageResponse(BoardImage boardImage) {
        this(boardImage.getId(), boardImage.getName(), boardImage.getSize(), readImageBytes(boardImage.getPath()));
    }

    private static byte[] readImageBytes(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image bytes", e);
        }
    }
}
