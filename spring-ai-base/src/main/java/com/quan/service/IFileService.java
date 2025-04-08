package com.quan.service;

import org.springframework.ai.document.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileService {

    /**
     * 文档解析
     * @param doc
     * @return
     */
    List<Document> docReader(MultipartFile doc);
}
