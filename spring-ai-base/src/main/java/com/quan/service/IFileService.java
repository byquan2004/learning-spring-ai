package com.quan.service;

import org.springframework.core.io.Resource;

public interface IFileService {

    /**
     * 文档向量化
     * @param doc
     * @return
     */
    void docReader(Resource doc);

    Resource getFile(String chatId);

    boolean save(String chatId, Resource resource);
}
