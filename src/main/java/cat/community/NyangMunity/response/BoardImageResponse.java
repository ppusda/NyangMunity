package cat.community.NyangMunity.response;

import cat.community.NyangMunity.domain.BoardImage;
import lombok.Getter;

import java.nio.file.Files;
import java.nio.file.Paths;

@Getter
public class BoardImageResponse {
    private Long id;
    private String name;
    private Long size;
    private byte[] imageBytes;

    public BoardImageResponse(BoardImage boardImage) {
        this.id = boardImage.getId();
        this.name = boardImage.getName();
        this.size = boardImage.getSize();
        try{
            byte[] imageByte = Files.readAllBytes(Paths.get(boardImage.getPath()));
            this.imageBytes = imageByte;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
// 무한루프로 인한 BoardImageDTO 작성
