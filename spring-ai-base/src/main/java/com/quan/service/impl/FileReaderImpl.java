package com.quan.service.impl;

import com.quan.service.IFileService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileReaderImpl implements IFileService {
    @Override
    public List<Document> docReader(MultipartFile doc) {
        if(isPdf(doc)){
            PagePdfDocumentReader documentReader = new PagePdfDocumentReader(doc.getResource(),
                    PdfDocumentReaderConfig
                            .builder()
                            .withPageTopMargin(0)
                            .withPageExtractedTextFormatter(
                                    ExtractedTextFormatter
                                            .builder()
                                            .withNumberOfTopTextLinesToDelete(0)
                                            .build())
                            .withPagesPerDocument(1)
                            .build());
            return documentReader.read();
        }else if(isMD(doc)) {
            return new MarkdownDocumentReader(doc.getResource(), MarkdownDocumentReaderConfig.defaultConfig()).get();
        }
        return null;

    }

    private boolean isPdf(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null && filename.toLowerCase().endsWith(".pdf");
    }
    private boolean isMD(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null && filename.toLowerCase().matches(".*\\.(md|markdown)");
    }

}
