package ru.beartrack.web.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageioUtil {
    private static final String BASE_PATH = "./temp/img/";

    public static Mono<File> saveImage(FilePart filePart, String fileName) throws IOException {
        // Ensure the directory exists
        Files.createDirectories(Paths.get(BASE_PATH));
        // Save the original file
        File savedFile = new File(BASE_PATH + fileName);
        return filePart.transferTo(savedFile).then(Mono.just(savedFile));
    }

    public static void createResizedImages(File imageFile, String fileName) throws IOException {
        BufferedImage originalImage = ImageIO.read(imageFile);

        int[] widths = {300, 640, 1280};
        for (int width : widths) {
            int height = (int) (originalImage.getHeight() * (width / (double) originalImage.getWidth()));
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            resizedImage.getGraphics().drawImage(originalImage, 0, 0, width, height, null);

            String resizedFileName = FilenameUtils.removeExtension(fileName) + "-" + width + ".webp";
            File resizedFile = new File(BASE_PATH + resizedFileName);

            saveAsWebP(resizedImage, resizedFile);
        }
    }

    private static void saveAsWebP(BufferedImage image, File file) throws IOException {
        ImageIO.write(image, "webp", file);
    }
}
