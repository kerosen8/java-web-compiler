package com.application.service;

import com.application.dao.CodeDAO;
import com.application.dto.CreateCodeDTO;
import com.application.dto.CodeDTO;
import com.application.entity.Code;
import com.application.util.annotation.Inject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class CodeService {

    @Inject
    private CodeDAO codeDAO;

    public Code create(CreateCodeDTO createFavoriteDTO) {
        return codeDAO.create(Code
                .builder()
                .userId(createFavoriteDTO.getUserId())
                .path(createFavoriteDTO.getPath())
                .title(createFavoriteDTO.getTitle())
                .build());
    }

    public List<CodeDTO> findByUserId(Integer userId) throws IOException {
        return codeDAO.findByUserId(userId).stream().map(i -> CodeDTO
                .builder()
                .code(getFileContents(i.getPath()))
                .title(i.getTitle())
                .build()).collect(Collectors.toList());
    }


    private String getFileContents(String path) {
        try {
            return new String(Files.readAllBytes(Path.of(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
