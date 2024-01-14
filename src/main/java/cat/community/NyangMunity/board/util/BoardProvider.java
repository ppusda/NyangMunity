package cat.community.NyangMunity.board.util;

import cat.community.NyangMunity.global.config.AppConfig;
import cat.community.NyangMunity.board.entity.BoardImage;
import cat.community.NyangMunity.global.exception.InvalidFileTypeException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class BoardProvider {

    private String imagePath;
    private String[] extensions = {"jpg", "png", "gif", "bmp", "svg", "jfif", "ico"};

    public BoardProvider(AppConfig appConfig) {
        this.imagePath = appConfig.getImagePath();
    }

    @Transactional
    public ArrayList<BoardImage> getImageList(List<MultipartFile> inputImageList) throws IOException {
        ArrayList<BoardImage> boardImages = new ArrayList<>();

        // todo file service 향후 적용 해보자
        for(MultipartFile file : inputImageList) {
            String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')+1).toLowerCase(Locale.ROOT);
            if(Arrays.asList(extensions).contains(fileExtension)){
                BoardImage boardImage = BoardImage.builder()
                        .name(file.getOriginalFilename())
                        .path(imagePath+ file.getOriginalFilename())
                        .size(file.getSize())
                        .build();

                byte[] bytes = file.getBytes();
                Path path = Paths.get(imagePath + file.getOriginalFilename());
                Files.write(path, bytes);
                log.info("Path: " + imagePath + file.getOriginalFilename() + "에 저장 완료했습니다.");

                boardImages.add(boardImage);
            } else {
                throw new InvalidFileTypeException();
            }
        }

        return boardImages;
    }
}
