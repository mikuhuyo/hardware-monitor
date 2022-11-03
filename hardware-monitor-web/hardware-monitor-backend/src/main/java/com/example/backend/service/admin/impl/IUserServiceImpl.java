package com.example.backend.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.admin.dto.LoginResultDTO;
import com.example.admin.pojo.User;
import com.example.admin.vo.LoginVO;
import com.example.admin.vo.UserVO;
import com.example.backend.mapper.admin.UserMapper;
import com.example.backend.service.admin.IUserService;
import com.example.common.exception.BusinessException;
import com.example.common.utils.MD5Util;
import com.example.common.utils.RSAUtil;
import com.example.common.utils.SaltUtil;
import com.example.common.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Slf4j
@Service
public class IUserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public LoginResultDTO userLogin(LoginVO loginVO) throws Exception {
        log.info("用户 {} 开始登录", loginVO.getLoginName());

        String decrypt = null;
        try {
            decrypt = RSAUtil.decrypt(loginVO.getPassword());
        } catch (Exception e) {
            throw new BusinessException("解码失败");
        }

        LambdaQueryWrapper<User> lambda = new QueryWrapper<User>().lambda().eq(User::getLoginName, loginVO.getLoginName());
        User user = userMapper.selectOne(lambda);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!"0".equals(user.getStatus())) {
            throw new BusinessException("用户已被冻结");
        }
        if (!MD5Util.verify(decrypt, user.getPassword(), user.getSalt(), "UTF-8")) {
            throw new BusinessException("密码错误");
        }

        LoginResultDTO dto = new LoginResultDTO();
        dto.setUserId(user.getId());
        dto.setLoginSuccess("0".equals(user.getStatus()));
        dto.setToken(TokenUtil.generateToken(user.getId()));

        log.info("用户 {} 登录成功", loginVO.getLoginName());

        return dto;
    }

    @Override
    public void userCreate(UserVO userVO) throws BusinessException {
        log.info("用户 {} 创建开始", userVO.getLoginName());

        String decrypt = null;
        try {
            decrypt = RSAUtil.decrypt(userVO.getPassword());
        } catch (Exception e) {
            throw new BusinessException("解码失败");
        }

        LambdaQueryWrapper<User> lambda = new QueryWrapper<User>().lambda().eq(User::getLoginName, userVO.getLoginName());
        boolean flag = userMapper.selectCount(lambda) != 0;
        if (flag) {
            throw new BusinessException("用户已存在");
        }

        String salt = SaltUtil.getRandomSalt(6);
        String sign = MD5Util.sign(decrypt, salt, "UTF-8");

        User user = new User();
        BeanUtils.copyProperties(userVO, user);
        user.setPassword(sign);
        user.setSalt(salt);

        userMapper.insert(user);

        log.info("用户 {} 创建成功", userVO.getLoginName());
    }
}
