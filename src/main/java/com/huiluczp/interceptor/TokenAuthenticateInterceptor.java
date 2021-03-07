package com.huiluczp.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huiluczp.bean.UserInfo;
import com.huiluczp.controller.UserController;
import com.huiluczp.mapper.UserLoginMapper;
import com.huiluczp.util.JWTUtil;
import org.apache.catalina.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenAuthenticateInterceptor implements HandlerInterceptor {

    @Override
    // 在这个方法中拦截请求，对token进行验证
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 跨域请求处理，设置头信息
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST,OPTIONS,PUT,HEAD");
        response.addHeader("Access-Control-Max-Age", "3600000");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Headers", "token"); // 设置头部可携带token
        // 禁止跨域缓存
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // 浏览器预检
        if (request.getMethod().equals("OPTIONS"))
            response.setStatus(HttpServletResponse.SC_OK);

        // 获取token，分割为token和refresh_token
        // token未过期，直接放行；token过期，refresh_token未过期，生成新的token和refresh_token并放行;
        // refresh_token过期，直接回登录页面
        String tokens = request.getHeader("token");
        if(tokens != null){
            if(tokens.contains(";")){
                String token = tokens.substring(0, tokens.indexOf(";"));
                String refresh_token = tokens.substring(tokens.indexOf(";") + 1);
                // 对token鉴定
                if(JWTUtil.verifyToken(token)){
                    // 有效,放行
                    response.setHeader("token", tokens);
                    return true;
                }else if(JWTUtil.verifyToken(refresh_token)){
                    // 有效，为活跃用户，更新token组合
                    String userId = JWTUtil.getDecodedId(refresh_token);
                    System.out.println(userId);
                    String newToken = JWTUtil.createToken(userId);
                    response.setHeader("token", newToken);
                    return true;
                }else{
                    response.sendRedirect(request.getContextPath() + "/login");
                }
            }else{
                response.sendRedirect(request.getContextPath() + "/login");
            }
        }else{
            System.out.println(request.getContextPath());
            response.sendRedirect(request.getContextPath() + "/login");
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
