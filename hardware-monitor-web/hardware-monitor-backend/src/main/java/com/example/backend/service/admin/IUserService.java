package com.example.backend.service.admin;

import com.example.admin.dto.LoginResultDTO;
import com.example.admin.vo.LoginVO;
import com.example.admin.vo.UserVO;
import com.example.common.exception.BusinessException;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
public interface IUserService {
    /**
     * 用户登录
     *
     * @param loginVO com.example.admin.vo.LoginVO
     * @return com.example.admin.dto.LoginResultDTO
     * @throws BusinessException 自定义业务异常
     */
    LoginResultDTO userLogin(LoginVO loginVO) throws Exception;

    /**
     * 创建用户
     *
     * @param userVO com.example.admin.vo.UserVO
     * @throws BusinessException 自定义业务异常
     */
    void userCreate(UserVO userVO) throws BusinessException;
}
