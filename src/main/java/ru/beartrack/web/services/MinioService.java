package ru.beartrack.web.services;

import io.minio.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Service
@PropertySource("classpath:application.properties")
public class MinioService {
    private final String bucket;
    private final MinioClient minioClient;

    @SneakyThrows
    public MinioService(MinioClient minioClient, @Value("${minio.bucket}") String bucket) {
        this.minioClient = minioClient;
        this.bucket = bucket;
        if (!this.minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            this.minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    public String uploadFile(File file, String objectName) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            ObjectWriteResponse response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(fileInputStream, file.length(), -1)
                            .contentType("image/webp")
                            .build()
            );
            log.info("minio response [{}]", response);
            return response.object();
        } catch (Exception e) {
            log.error("error [{}]",e);
            return "";
        }
    }
}
