package com.example.backend.filter;

import com.example.common.exception.BusinessException;
import com.example.common.utils.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Component
public class RequestFilter extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 发起预检请求直接放行
        if ("OPTIONS".equals(request.getMethod().toUpperCase())) {
            return true;
        }

        if ("/api/login".equals(request.getServletPath().toLowerCase())) {
            return true;
        }

        if ("/api/device/client".equals(request.getServletPath().toLowerCase())) {
            return true;
        }

        if (request.getServletPath().toLowerCase().contains("rsa-key")) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (StringUtils.isNullOrEmpty(authorization)) {
            throw new BusinessException("用户未登录");
        }

        try {
            Long aLong = TokenUtil.verifyToken(authorization);
        } catch (Exception e) {
            throw new BusinessException("无效的用户登录");
        }

        return true;
    }

}
