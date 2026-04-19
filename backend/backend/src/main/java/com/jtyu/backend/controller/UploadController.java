package com.jtyu.backend.controller;

import com.jtyu.backend.model.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${file.base-path}")
    private String basePath;

    @PostMapping
    public Result upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }

        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID() + suffix;

            // 拼接最终路径
            String uploadDir = basePath + "/uploads/";

            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            File dest = new File(uploadDir + fileName);

            // 调试用（建议保留一开始用）
            System.out.println("上传路径: " + dest.getAbsolutePath());

            file.transferTo(dest);

            // 返回访问 URL
            String url = "http://localhost:8080/uploads/" + fileName;

            return Result.success("上传成功", url);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }
}