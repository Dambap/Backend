package com.double_o.dambap.s3.presentation;

import static com.double_o.dambap.s3.utils.MediaConstants.MEDIA_MAX_SIZE;

import com.double_o.dambap.common.model.ResponseDto;
import com.double_o.dambap.exception.dto.ErrorType;
import com.double_o.dambap.exception.s3.S3InvalidException;
import com.double_o.dambap.s3.application.S3Uploader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/images")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Uploader s3Uploader;

    @Operation(summary = "이미지 업로드", description = "S3를 이용한 이미지 업로드 전용 api")
    @ApiResponse(responseCode = "200", description = "이미지 업로드 성공, 이미지 url 반환")
    @PostMapping
    public ResponseEntity<?> uploadImages(@RequestParam("images") List<MultipartFile> images) {
        try {
            validateEmptyFiles(images);
            validateMediaMaxSize(images);

            List<String> mediaUrls = images.stream()
                    .map(s3Uploader::uploadFile)
                    .toList();

            log.debug("Successfully uploaded image. URL: {}", mediaUrls.size());
            return ResponseDto.ok(mediaUrls);
        } catch (Exception e) {
            log.error("Failed to upload images", e);
            throw new S3InvalidException(ErrorType.IMAGE_UPLOAD_FAILED_ERROR);
        }
    }


    /** 파일이 비었는지 검사 */
    private void validateEmptyFiles(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new S3InvalidException(ErrorType.EMPTY_IMAGE_ERROR);
        }
        if (images.stream().anyMatch(img -> img.isEmpty() || img.getSize() == 0)) {
            throw new S3InvalidException(ErrorType.EMPTY_IMAGE_ERROR);
        }
    }

    /** 파일 개수 검사 (최대 MEDIA_MAX_SIZE) */
    private void validateMediaMaxSize(List<MultipartFile> images) {
        if (images.size() > MEDIA_MAX_SIZE) {
            throw new S3InvalidException(ErrorType.MEDIA_MAX_SIZE_3_ERROR);
        }
    }
}
