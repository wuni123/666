package com.rural.agri.controller;

import com.rural.agri.common.BizException;
import com.rural.agri.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 图片上传：管理员走 /api/admin/upload/image，农户走 /api/user/upload/image，均需登录。
 */
@RestController
public class UploadController {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/api/admin/upload/image")
    public Result<String> uploadImageAdmin(@RequestParam("file") MultipartFile file) {
        return doUpload(file);
    }

    @PostMapping("/api/user/upload/image")
    public Result<String> uploadImageUser(@RequestParam("file") MultipartFile file) {
        return doUpload(file);
    }

    private Result<String> doUpload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BizException("文件不能为空");
        }

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
        }
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BizException("仅支持 jpg/jpeg/png/gif/webp 图片");
        }

        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
            file.transferTo(dir.resolve(filename).toFile());
            return Result.success("/upload/" + filename);
        } catch (Exception e) {
            throw new BizException("文件上传失败");
        }
    }
}
