package com.application.service;

import com.application.dao.CodeDAO;
import com.application.dto.CreateCodeDTO;
import com.application.dto.CodeDTO;
import com.application.entity.Code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class CodeService {

    private final CodeDAO favoriteDAO = new CodeDAO();

    public Code create(CreateCodeDTO createFavoriteDTO) {
        return favoriteDAO.create(Code
                .builder()
                .userId(createFavoriteDTO.getUserId())
                .path(createFavoriteDTO.getPath())
                .title(createFavoriteDTO.getTitle())
                .build());
    }

    public List<CodeDTO> findByUserId(Integer userId) throws IOException {
        return favoriteDAO.findByUserId(userId).stream().map(i -> CodeDTO
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
