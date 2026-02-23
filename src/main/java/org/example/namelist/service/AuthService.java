package org.example.namelist.service;

import org.example.namelist.entity.AdminUser;
import org.example.namelist.mapper.AdminUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证服务
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AdminUserMapper adminUserMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 根据用户名查询管理员
     */
    public AdminUser findByUsername(String username) {
        return adminUserMapper.selectByUsername(username);
    }

    /**
     * 验证密码
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 登录验证
     */
    public AdminUser login(String username, String password) {
        AdminUser user = adminUserMapper.selectByUsername(username);
        if (user == null) {
            logger.warn("登录失败: 用户不存在 - {}", username);
            return null;
        }

        if (user.getStatus() != 1) {
            logger.warn("登录失败: 用户已禁用 - {}", username);
            return null;
        }

        if (!verifyPassword(password, user.getPassword())) {
            logger.warn("登录失败: 密码错误 - {}", username);
            return null;
        }

        logger.info("登录成功: {}", username);
        return user;
    }

    /**
     * 获取所有管理员
     */
    public List<AdminUser> getAllAdmins() {
        return adminUserMapper.selectList(null);
    }

    /**
     * 添加管理员
     */
    public AdminUser addAdmin(AdminUser adminUser) {
        // 加密密码
        adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
        adminUser.setStatus(1);

        adminUserMapper.insert(adminUser);
        logger.info("添加管理员: {}", adminUser.getUsername());
        return adminUser;
    }

    /**
     * 更新管理员
     */
    public boolean updateAdmin(AdminUser adminUser) {
        // 如果密码不为空，则更新密码
        if (adminUser.getPassword() != null && !adminUser.getPassword().isEmpty()) {
            adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
        }
        int result = adminUserMapper.updateById(adminUser);
        if (result > 0) {
            logger.info("更新管理员: {}", adminUser.getUsername());
            return true;
        }
        return false;
    }

    /**
     * 删除管理员（软删除）
     */
    public boolean deleteAdmin(Integer id) {
        int result = adminUserMapper.deleteById(id);
        if (result > 0) {
            logger.info("删除管理员: {}", id);
            return true;
        }
        return false;
    }

    /**
     * 编码密码
     */
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 检查用户名是否存在
     */
    public boolean isUsernameExists(String username) {
        AdminUser user = adminUserMapper.selectByUsername(username);
        return user != null;
    }
}
