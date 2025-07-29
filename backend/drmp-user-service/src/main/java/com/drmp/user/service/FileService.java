package com.drmp.user.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
public interface FileService {
    
    /**
     * 上传文件
     *
     * @param file 文件
     * @param category 文件分类
     * @return 文件路径
     */
    String uploadFile(MultipartFile file, String category);
    
    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean deleteFile(String filePath);
    
    /**
     * 获取文件URL
     *
     * @param filePath 文件路径
     * @return 文件URL
     */
    String getFileUrl(String filePath);
}