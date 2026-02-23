package org.example.namelist.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import org.example.namelist.config.OssConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

/**
 * 阿里云OSS服务
 */
@Service
public class OssService {

    private static final Logger logger = LoggerFactory.getLogger(OssService.class);

    @Autowired
    private OssConfig ossConfig;

    /**
     * 获取OSS客户端
     */
    private OSS getOssClient() {
        // 验证配置
        if (ossConfig.getEndpoint() == null || ossConfig.getEndpoint().isEmpty()) {
            throw new RuntimeException("OSS endpoint 未配置");
        }
        if (ossConfig.getAccessKeyId() == null || ossConfig.getAccessKeyId().isEmpty()) {
            throw new RuntimeException("OSS access-key-id 未配置");
        }
        if (ossConfig.getAccessKeySecret() == null || ossConfig.getAccessKeySecret().isEmpty()) {
            throw new RuntimeException("OSS access-key-secret 未配置");
        }

        return new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
        );
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @param folder 文件夹路径
     * @return 文件的访问URL
     */
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            logger.warn("上传文件为空");
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "unknown";
        }

        // 生成唯一的文件名
        String extension = getFileExtension(originalFilename);
        String newFileName = UUID.randomUUID().toString().replace("-", "") + extension;

        // 构建完整的文件路径
        String filePath = folder + "/" + newFileName;

        try (InputStream inputStream = file.getInputStream()) {
            // 上传文件
            uploadInputStream(inputStream, filePath, file.getContentType());

            // 返回文件的访问URL
            String fileUrl = ossConfig.getBucketDomain() + "/" + filePath;
            logger.info("文件上传成功: {}", fileUrl);

            return fileUrl;

        } catch (IOException e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 上传文件（输入流方式）
     *
     * @param inputStream 输入流
     * @param filePath    文件路径
     * @param contentType 内容类型
     */
    private void uploadInputStream(InputStream inputStream, String filePath, String contentType) {
        OSS ossClient = getOssClient();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossConfig.getBucketName(),
                    filePath,
                    inputStream,
                    metadata
            );

            ossClient.putObject(putObjectRequest);
            logger.debug("文件上传到OSS: {}", filePath);

        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 上传字节数组
     *
     * @param bytes      字节数组
     * @param filePath   文件路径
     * @param contentType 内容类型
     * @return 文件的访问URL
     */
    public String uploadBytes(byte[] bytes, String filePath, String contentType) {
        if (bytes == null || bytes.length == 0) {
            logger.warn("上传字节数组为空");
            return null;
        }

        OSS ossClient = getOssClient();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(bytes.length);

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossConfig.getBucketName(),
                    filePath,
                    new java.io.ByteArrayInputStream(bytes),
                    metadata
            );

            ossClient.putObject(putObjectRequest);

            // 返回文件的访问URL
            String fileUrl = ossConfig.getBucketDomain() + "/" + filePath;
            logger.info("字节数组上传成功: {}", fileUrl);

            return fileUrl;

        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            logger.warn("删除文件URL为空");
            return false;
        }

        // 从URL中提取文件路径
        String filePath = extractFilePath(fileUrl);
        if (filePath == null) {
            logger.warn("无法解析文件路径: {}", fileUrl);
            return false;
        }

        OSS ossClient = getOssClient();
        try {
            ossClient.deleteObject(ossConfig.getBucketName(), filePath);
            logger.info("文件删除成功: {}", filePath);
            return true;
        } catch (Exception e) {
            logger.error("文件删除失败: {}", e.getMessage(), e);
            return false;
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param fileUrl 文件URL
     * @return 是否存在
     */
    public boolean fileExists(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        String filePath = extractFilePath(fileUrl);
        if (filePath == null) {
            return false;
        }

        OSS ossClient = getOssClient();
        try {
            return ossClient.doesObjectExist(ossConfig.getBucketName(), filePath);
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 获取文件输入流
     *
     * @param fileUrl 文件URL
     * @return 输入流
     */
    public InputStream getFileInputStream(String fileUrl) {
        String filePath = extractFilePath(fileUrl);
        if (filePath == null) {
            return null;
        }

        OSS ossClient = getOssClient();
        try {
            OSSObject object = ossClient.getObject(ossConfig.getBucketName(), filePath);
            return object.getObjectContent();
        } catch (Exception e) {
            logger.error("获取文件流失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从URL中提取文件路径
     *
     * @param fileUrl 文件URL
     * @return 文件路径（不带前导斜杠）
     */
    private String extractFilePath(String fileUrl) {
        if (fileUrl == null) {
            return null;
        }

        String filePath = null;

        // 移除域名部分
        String domain = ossConfig.getBucketDomain();
        if (fileUrl.startsWith(domain)) {
            filePath = fileUrl.substring(domain.length());
        } 
        // 如果是完整URL，尝试解析
        else if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
            // 提取bucket和endpoint部分
            String bucketDomain = ossConfig.getBucketName() + "." + ossConfig.getEndpoint();
            if (fileUrl.contains(bucketDomain)) {
                int index = fileUrl.indexOf(bucketDomain);
                filePath = fileUrl.substring(index + bucketDomain.length());
            }
        } 
        // 假设传入的就是文件路径
        else {
            filePath = fileUrl;
        }

        // 移除前导斜杠
        if (filePath != null && filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }

        return filePath;
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 生成缩略图URL（阿里云OSS支持图片处理）
     *
     * @param fileUrl  原图URL
     * @param width   宽度
     * @param height  高度
     * @return 处理后的URL
     */
    public String getThumbnailUrl(String fileUrl, int width, int height) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        // 阿里云OSS图片处理语法
        String domain = ossConfig.getBucketDomain();
        if (fileUrl.startsWith(domain)) {
            String filePath = fileUrl.substring(domain.length());
            return generateSignedUrl(filePath) + "&x-oss-process=image/resize,w_" + width + ",h_" + height + ",m_fill";
        }

        return fileUrl;
    }

    /**
     * 生成文件的签名URL（用于访问私有文件）
     *
     * @param fileUrl 文件URL
     * @param expirationMinutes 签名有效期（分钟）
     * @return 签名后的URL
     */
    public String generateSignedUrl(String fileUrl, int expirationMinutes) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        // 提取文件路径
        String filePath = extractFilePath(fileUrl);
        if (filePath == null || filePath.isEmpty()) {
            return fileUrl;
        }

        OSS ossClient = getOssClient();
        try {
            // 设置签名URL的有效时间
            java.util.Date expiration = new java.util.Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000L);
            URL signedUrl = ossClient.generatePresignedUrl(ossConfig.getBucketName(), filePath, expiration);
            logger.debug("生成签名URL: {}", signedUrl);
            return signedUrl.toString();
        } catch (Exception e) {
            logger.error("生成签名URL失败: {}", e.getMessage(), e);
            return fileUrl;
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 生成文件的签名URL（默认30分钟有效）
     *
     * @param fileUrl 文件URL
     * @return 签名后的URL
     */
    public String generateSignedUrl(String fileUrl) {
        return generateSignedUrl(fileUrl, 30);
    }
}
