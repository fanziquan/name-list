package org.example.namelist.controller;

import javax.servlet.http.HttpSession;
import org.example.namelist.entity.AdminUser;
import org.example.namelist.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理后台登录、登出等认证相关请求
 */
@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 登录接口
     */
    @ResponseBody
    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();

        try {
            AdminUser user = authService.login(username, password);

            if (user != null) {
                // 设置 Spring Security 认证上下文
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                );
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);

                // 将用户信息存入session
                session.setAttribute("SPRING_SECURITY_CONTEXT", context);
                session.setAttribute("adminUser", user);
                session.setAttribute("username", user.getUsername());
                session.setAttribute("nickname", user.getNickname());
                session.setAttribute("role", user.getRole());

                result.put("code", 200);
                result.put("message", "登录成功");
                result.put("username", user.getUsername());
                result.put("nickname", user.getNickname());
                result.put("role", user.getRole());
            } else {
                result.put("code", 401);
                result.put("message", "用户名或密码错误");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "登录失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 登出接口
     */
    @ResponseBody
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        // 清除 Spring Security 上下文
        SecurityContextHolder.clearContext();

        // 清除session
        session.invalidate();

        result.put("code", 200);
        result.put("message", "登出成功");

        return result;
    }

    /**
     * 检查登录状态
     */
    @ResponseBody
    @GetMapping("/check")
    public Map<String, Object> checkLogin(HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        Object username = session.getAttribute("username");
        if (username != null) {
            result.put("code", 200);
            result.put("loggedIn", true);
            result.put("username", username);
            result.put("nickname", session.getAttribute("nickname"));
            result.put("role", session.getAttribute("role"));
        } else {
            result.put("code", 200);
            result.put("loggedIn", false);
        }

        return result;
    }

    /**
     * 跳转登录页
     */
    @GetMapping("/toLogin")
    public String toLogin() {
        return "admin/login";
    }
}
