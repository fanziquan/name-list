package org.example.namelist.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 阿里云OSS配置类
 */
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
@PropertySource("classpath:application-oss.properties")
public class OssConfig {

    private static final Logger logger = LoggerFactory.getLogger(OssConfig.class);

    /**
     * OSS访问端点
     */
    private String endpoint;

    /**
     * AccessKey ID
     */
    private String accessKeyId;

    /**
     * AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * Bucket名称
     */
    private String bucketName;

    /**
     * 文件存储目录
     */
    private String folder;

    /**
     * 访问域名
     */
    private String domain;

    /**
     * 初始化时打印配置信息（不打印密钥）
     */
    public OssConfig() {
        logger.info("OSS配置已加载 - endpoint: {}, bucket: {}, folder: {}",
                endpoint, bucketName, folder);
    }

    // ==================== Getters and Setters ====================

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * 获取完整的Bucket域名
     */
    public String getBucketDomain() {
        if (domain != null && !domain.isEmpty()) {
            return domain;
        }
        return "https://" + bucketName + "." + endpoint;
    }
}
