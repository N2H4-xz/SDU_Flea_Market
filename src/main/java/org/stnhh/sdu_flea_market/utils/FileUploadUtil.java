package org.stnhh.sdu_flea_market.utils;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具类
 * 用于处理文件上传到 /www/wwwroot/files 目录
 */
public class FileUploadUtil {

    // 文件保存目录
    private static final String UPLOAD_DIR = "/www/wwwroot/files";

    // 允许的文件扩展名
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp"};

    // 最大文件大小 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 上传单个文件
     *
     * @param file 上传的文件
     * @return 保存的文件名
     * @throws IOException 文件操作异常
     */
    public static String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过 10MB");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("无法获取文件名");
        }

        // 验证文件扩展名
        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedExtension(fileExtension)) {
            throw new IllegalArgumentException("不支持的文件类型: " + fileExtension);
        }

        // 生成新的文件名 (UUID + 原始扩展名)
        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;

        // 创建上传目录
        File uploadDirectory = new File(UPLOAD_DIR);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        // 保存文件
        File destinationFile = new File(uploadDirectory, newFileName);
        file.transferTo(destinationFile);

        return newFileName;
    }

    /**
     * 上传多个文件
     *
     * @param files 上传的文件列表
     * @return 保存的文件名列表
     * @throws IOException 文件操作异常
     */
    public static List<String> uploadFiles(MultipartFile[] files) throws IOException {
        List<String> fileNames = new ArrayList<>();

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("文件列表不能为空");
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = uploadFile(file);
                fileNames.add(fileName);
            }
        }

        if (fileNames.isEmpty()) {
            throw new IllegalArgumentException("没有有效的文件被上传");
        }

        return fileNames;
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     * @return 是否删除成功
     */
    public static boolean deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        File file = new File(UPLOAD_DIR, fileName);
        return file.exists() && file.delete();
    }

    /**
     * 删除多个文件
     *
     * @param fileNames 文件名列表
     * @return 删除成功的文件数
     */
    public static int deleteFiles(List<String> fileNames) {
        int deletedCount = 0;

        if (fileNames == null || fileNames.isEmpty()) {
            return 0;
        }

        for (String fileName : fileNames) {
            if (deleteFile(fileName)) {
                deletedCount++;
            }
        }

        return deletedCount;
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名 (小写)
     */
    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 检查文件扩展名是否被允许
     *
     * @param extension 文件扩展名
     * @return 是否被允许
     */
    private static boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件的完整路径
     *
     * @param fileName 文件名
     * @return 完整路径
     */
    public static String getFilePath(String fileName) {
        return UPLOAD_DIR + File.separator + fileName;
    }

    /**
     * 获取文件的访问 URL
     *
     * @param fileName 文件名
     * @return 访问 URL
     */
    public static String getFileUrl(String fileName) {
        return "/files/" + fileName;
    }
}

