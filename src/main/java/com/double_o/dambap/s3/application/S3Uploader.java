package com.double_o.dambap.s3.application;

import com.double_o.dambap.s3.utils.CommonUtils;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            log.warn("image is null");
            return "";
        }

        try {
            // UUID 와 파일 확장자를 결합하여 고유한 파일명 생성
            String originalFilename = multipartFile.getOriginalFilename();
            String fileName = CommonUtils.buildFileName(originalFilename);

            // 파일 업로드를 위한 요청 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(multipartFile.getContentType())
                    .contentLength(multipartFile.getSize())
                    .acl("public-read") // 공개 읽기 권한 추가
                    .build();

            // 파일 업로드
            RequestBody requestBody = RequestBody.fromBytes(multipartFile.getBytes());
            s3Client.putObject(putObjectRequest, requestBody);

            // URL 생성하여 반환
            return String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName,
                    s3Client.serviceClientConfiguration().region().toString(),
                    fileName);

        } catch (IOException e) {
            log.error("Failed to upload file to S3", e);
            throw new RuntimeException("파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }
}