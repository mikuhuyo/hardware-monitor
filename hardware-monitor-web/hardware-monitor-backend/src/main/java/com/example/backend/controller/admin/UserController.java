package com.example.backend.controller.admin;

import com.example.admin.dto.LoginResultDTO;
import com.example.admin.vo.LoginVO;
import com.example.admin.vo.UserVO;
import com.example.backend.service.admin.IUserService;
import com.example.common.domain.RestResponse;
import com.example.common.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private IUserService iUserService;

    @GetMapping("/users/info")
    public RestResponse<String> userInfo() {
        return RestResponse.success();
    }

    @DeleteMapping("/users/logout")
    public RestResponse<String> userLogout() {
        return RestResponse.success();
    }

    @PostMapping("/login")
    public LoginResultDTO userLogin(@RequestBody LoginVO loginVO) throws Exception {
        return iUserService.userLogin(loginVO);
    }

    @PostMapping("/users")
    public RestResponse<String> userCreate(@RequestBody UserVO userVO) {
        iUserService.userCreate(userVO);

        return RestResponse.success();
    }

    @GetMapping("/rsa-key/{password}")
    public RestResponse<String> encodePassword(@PathVariable("password") String password) throws Exception {
        return RestResponse.success(RSAUtil.encrypt(password));
    }

    @GetMapping("/rsa-key")
    public RestResponse<String> publicKeyRsa() {
        return RestResponse.success(RSAUtil.getPublicKeyStr());
    }
}
