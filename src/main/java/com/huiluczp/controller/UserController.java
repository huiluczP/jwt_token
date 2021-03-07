package com.huiluczp.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huiluczp.bean.UserInfo;
import com.huiluczp.bean.UserLogin;
import com.huiluczp.mapper.UserInfoMapper;
import com.huiluczp.mapper.UserLoginMapper;
import com.huiluczp.util.JWTUtil;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
public class UserController {
    private final UserLoginMapper userLoginMapper;
    private final UserInfoMapper userInfoMapper;

    public UserController(UserLoginMapper userLoginMapper, UserInfoMapper userInfoMapper) {
        this.userLoginMapper = userLoginMapper;
        this.userInfoMapper = userInfoMapper;
    }

    @RequestMapping("/login")
    public String loginPage(){
        return "/login.html";
    }

    @RequestMapping(value = "/checkLogin", method = RequestMethod.POST)
    @ResponseBody
    // 用户登录验证，返回token
    public String userLoginCheck(@RequestParam("username") String userName, @RequestParam("password") String password, HttpServletRequest request){
        // 检验登录
        boolean isChecked = checkUser(userName, password);
        if(isChecked){
            // 获取用户id信息，作为JWT的payload，最终返回token
            UserInfo info = userInfoMapper.getUserInfo(userName);
            try {
                // 包括refresh token
                String token = JWTUtil.createToken(info.getUserId());
                return token;
            } catch (JsonProcessingException | UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    // 比对密码结果
    private boolean checkUser(String username, String password){
        UserLogin loginInfo = userLoginMapper.getUserLoginInfo(username);
        if(loginInfo != null){
            return loginInfo.getUserPassword().equals(password);
        }
        return false;
    }

    @RequestMapping(value = "/user/userInfo")
    @ResponseBody
    // 得到token中个人信息
    public String getUserInfo(HttpServletRequest request){
        // header中获取token信息
        String tokens = request.getHeader("token");
        String token = tokens.substring(0, tokens.indexOf(";"));
        try {
            return getDecodedUserInfo(token);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = {"/userInformation", "/"})
    // 显示用户信息
    public String getUserInformation(){
        return "/user/userInformation.html";
    }

    // 获取解码后的用户信息,这方法应该写到UserService里的但是咱没写这类,就先塞这里了
    // 因为咱把所有用户id放在token里了，这边得处理下用户日期问题
    // 太耦合了我要死了
    public String getDecodedUserInfo(String token) throws UnsupportedEncodingException {
        DecodedJWT dJwt = JWT.decode(token);
        String userId = dJwt.getClaim("userId").asString();
        UserInfo info = this.userInfoMapper.getUserInfo(userId);
        ObjectMapper om = new ObjectMapper();
        try {
            String userStr = om.writeValueAsString(info);
            Map<String, Object> maps = om.readValue(userStr, Map.class);
            String bir = maps.get("userBirthday").toString();
            Date date = new Date(Long.parseLong(bir));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String birStr = sdf.format(date);
            maps.put("userBirthday", birStr);
            return new ObjectMapper().writeValueAsString(maps);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

}
