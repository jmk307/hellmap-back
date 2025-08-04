package com.jimin.hellmap.global.config.s3;

import com.jimin.hellmap.global.error.ErrorCode;
import com.jimin.hellmap.global.error.exception.BadRequestException;
import com.jimin.hellmap.global.error.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3ServiceImpl implements AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    private final S3Client s3Client;

    public List<String> uploadImage(List<MultipartFile> multipartFiles, String dirName) {
        List<String> fileNameList = new ArrayList<>();

        multipartFiles.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename(), dirName);

            try (InputStream inputStream = file.getInputStream()) {
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .contentLength(file.getSize())
                        .contentType(file.getContentType())
                        .build();

                s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, file.getSize()));
            } catch (IOException e) {
                throw new InternalServerException(ErrorCode.FILE_UPLOAD_FAILED);
            }

            fileNameList.add(getObjectUrl(fileName));
        });

        return fileNameList;
    }

    public String uploadImage(MultipartFile multipartFile, String dirName) {
        String fileName = createFileName(multipartFile.getOriginalFilename(), dirName);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentLength(multipartFile.getSize())
                    .contentType(multipartFile.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, multipartFile.getSize()));
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        return getObjectUrl(fileName);
    }

    public void deleteImage(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private String getObjectUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);
    }

    private String createFileName(String fileName, String dirName) {
        return dirName + "/" + UUID.randomUUID() + getFileExtension(fileName);
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new BadRequestException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }
}