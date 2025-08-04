package com.jimin.hellmap;


import com.jimin.hellmap.global.config.CommonApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "EB 헬스체크 컨트롤러(무시해주세용)")
public class HealthCheckController {
    @GetMapping
    public CommonApiResponse<Boolean> healthCheck() {
        return CommonApiResponse.of(true);
    }
}
