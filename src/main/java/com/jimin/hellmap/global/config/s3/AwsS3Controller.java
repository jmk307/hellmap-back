package com.jimin.hellmap.global.config.s3;

import com.jimin.hellmap.global.config.CommonApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Amazon S3 이미지 업로드 및 삭제 기능을 담당합니다
 */
@Slf4j
@RestController
@Tag(name = "이미지, 영상", description = "Amazon S3 이미지 업로드 및 삭제 API")
@RequiredArgsConstructor
@RequestMapping("/s3")
public class AwsS3Controller {

    private final AwsS3ServiceImpl awsS3ServiceImpl;

    /**
     * Amazon S3에 이미지 업로드
     * @return 성공 시 200 / 함께 업로드 된 파일의 파일명 리스트 반영
     */
    @Operation(summary = "Amazon S3에 이미지를 업로드한다", description = "Amazon S3에 이미지 업로드")
    @PostMapping(value = "/image/{dir}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public CommonApiResponse<List<String>> uploadImage(
            @PathVariable("dir") AwsS3Directory dirName,
            @RequestPart List<MultipartFile> multipartFiles
    ) {
        return CommonApiResponse.of(awsS3ServiceImpl.uploadImage(multipartFiles, dirName.name()));
    }

    /**
     * Amazon S3에 이미지 업로드 된 파일을 삭제
     * @return 성공 시 200 Success
     */
    @Operation(summary = "[Admin] Amazon S3에 업로드 된 파일을 삭제한다", description = "Amazon S3에 업로드된 이미지 삭제")
    @DeleteMapping("/image/{file}")
    public CommonApiResponse<Void> deleteImage(
            @PathVariable("file") String fileName
    ) {
        awsS3ServiceImpl.deleteImage(fileName);
        return CommonApiResponse.of(null);
    }
}