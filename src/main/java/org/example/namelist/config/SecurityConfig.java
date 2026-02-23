package org.example.namelist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletResponse;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 自定义认证失败处理器
     * 对于 API 请求返回 401 状态码，对于页面请求跳转到登录页
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            String path = request.getRequestURI();
            // 如果是 API 请求，返回 JSON 401
            if (path.startsWith("/api/")) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"code\":401,\"message\":\"未授权，请先登录\",\"path\":\"" + path + "\"}");
            } else {
                // 页面请求跳转到登录页
                response.sendRedirect("/api/auth/toLogin?expired=1");
            }
        };
    }

    /**
     * 配置安全过滤器链
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（简化开发）
            .csrf().disable()
            // 配置会话管理
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .and()
            // 配置权限
            .authorizeRequests()
                // 允许访问前台页面和静态资源
                .antMatchers("/", "/index", "/hero/**", "/villain/**", "/search").permitAll()
                .antMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                // 允许访问登录页面、登录接口和登录跳转
                .antMatchers("/admin/login", "/api/auth/toLogin", "/api/auth/login", "/api/auth/logout", "/api/auth/check").permitAll()
                // 允许访问人物 API 接口（前台首页加载数据用）
                .antMatchers("/api/hero", "/api/villain").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
            .and()
            // 配置认证失败处理器
            .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
            .and()
            // 配置登出
            .logout()
                .logoutUrl("/api/auth/logout")
                .logoutSuccessUrl("/api/auth/toLogin?logout")
                .permitAll();
    }
}
