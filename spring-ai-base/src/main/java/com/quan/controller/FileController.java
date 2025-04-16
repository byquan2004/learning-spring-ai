package com.quan.controller;

import com.quan.pojo.Result;
import com.quan.service.IFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/ai/pdf")
@RequiredArgsConstructor
public class FileController {

    private final IFileService fileService;

    /**
     * 上传文档向量化
     */
    @PostMapping("/upload/{chatId}")
    public Result uploadForVector(@PathVariable String chatId, @RequestParam("file") MultipartFile document) {
        try {
            // 1. 校验文件是否为PDF格式
            if (!Objects.equals(document.getContentType(), "application/pdf")) {
                return Result.fail("只能上传PDF文件！");
            }
            // 2.文件持久化
            boolean isSuccess = fileService.save(chatId, document.getResource());
            if (!isSuccess) {
                return Result.fail("保存pdf文件失败！");
            }
            // 3.文档向量化
            fileService.docReader(document.getResource());
            return Result.ok();
        } catch (Exception e) {
            log.error("Failed Upload: {}",e.getMessage());
            throw new RuntimeException("pdf上传失败");
        }
    }

    /**
     * 文件下载
     */
    @GetMapping("/file/{chatId}")
    public ResponseEntity<Resource> download(@PathVariable("chatId") String chatId) throws IOException {
        // 1.读取文件
        Resource resource = fileService.getFile(chatId);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        // 2.文件名编码，写入响应头
        String filename = URLEncoder.encode(Objects.requireNonNull(resource.getFilename()), StandardCharsets.UTF_8);
        // 3.返回文件
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

}

