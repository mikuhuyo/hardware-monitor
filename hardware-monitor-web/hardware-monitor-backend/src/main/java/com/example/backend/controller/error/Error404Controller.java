package com.example.backend.controller.error;

import com.example.common.domain.RestResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@RestController
public class Error404Controller implements ErrorController {
    private static final String ERROR_PATH = "/error";

    @RequestMapping(value = ERROR_PATH)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public RestResponse<Object> handleError() {
        return RestResponse.fail("资源丢失, 呜呜呜");
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}


