package ru.beartrack.web.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Slf4j
public class ImageioUtil {
    private static final String BASE_PATH = "./temp/img/";

    public static Mono<File> saveImage(FilePart filePart, String fileName) throws IOException {
        if(filePart != null) {
            // Ensure the directory exists
            Files.createDirectories(Paths.get(BASE_PATH));
            // Save the original file
            File savedFile = new File(BASE_PATH + fileName);
            return filePart.transferTo(savedFile).then(Mono.just(savedFile));
        }else{
            return Mono.empty();
        }
    }

    public static Mono<File> saveImage(String base64image, String type, String name) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64image);
        Path filePath = Paths.get(BASE_PATH + name + "." + type);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, decodedBytes);
        return Mono.justOrEmpty(filePath.toFile());
    }

    public static void createResizedImages(File imageFile, String fileName, String[] sizes) throws IOException {
        BufferedImage originalImage = ImageIO.read(imageFile);

        int[] widths = {Integer.parseInt(sizes[0]), Integer.parseInt(sizes[1]), Integer.parseInt(sizes[2])};
        for (int width : widths) {
            int height = (int) (originalImage.getHeight() * (width / (double) originalImage.getWidth()));
            /*BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            resizedImage.getGraphics().drawImage(originalImage, 0, 0, width, height, null);*/
            BufferedImage resizedImage = resizeImage(originalImage, width, height);

            String resizedFileName = FilenameUtils.removeExtension(fileName) + "-" + width + ".webp";
            File resizedFile = new File(BASE_PATH + resizedFileName);

            saveAsWebP(resizedImage, resizedFile);
        }
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private static void saveAsWebP(BufferedImage image, File file) throws IOException {
        ImageIO.write(image, "webp", file);
    }

    public static void releaseTemp(String fileName, String[] sizes) {
        try {
            Files.delete(Path.of(BASE_PATH + fileName));
            if(sizes != null) {
                Files.delete(Path.of(BASE_PATH + FilenameUtils.removeExtension(fileName) + "-" + sizes[0] + ".webp"));
                Files.delete(Path.of(BASE_PATH + FilenameUtils.removeExtension(fileName) + "-" + sizes[1] + ".webp"));
                Files.delete(Path.of(BASE_PATH + FilenameUtils.removeExtension(fileName) + "-" + sizes[2] + ".webp"));
            }
        } catch (IOException e) {
            log.error("Cant delete files! Error [{},{}]",e.getMessage(),e.getLocalizedMessage());
        }
    }
}
