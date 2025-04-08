package com.quan.controller;

import com.quan.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/ai/docs")
@RequiredArgsConstructor
public class FileController {

//    private final VectorStore vectorStore;

    private final IFileService fileService;

    /**
     * 上传文档向量化
     */
    @PostMapping
    public String uploadForVector(@RequestParam("file")MultipartFile file) {
        try {
            List<Document> documents = fileService.docReader(file);
//            vectorStore.add(documents);
            return "success";
        } catch (Exception e) {
            return "error," + e.getMessage();
        }

    }

}

