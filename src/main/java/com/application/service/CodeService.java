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

    public void create(CreateCodeDTO createFavoriteDTO) throws IOException {
         codeDAO.create(Code.builder()
                 .userId(createFavoriteDTO.getUserId())
                .path(createFavoriteDTO.getPath())
                .title(createFavoriteDTO.getTitle())
                .build());
         createCodeFile(createFavoriteDTO);
    }

    public List<CodeDTO> findCodesByUserId(Integer userId) throws IOException {
        return codeDAO.findCodesByUserId(userId).stream().map(i -> CodeDTO.builder()
                .id(i.getId())
                .code(getFileContent(i.getPath()))
                .title(i.getTitle())
                .build()).collect(Collectors.toList());
    }

    public void deleteCodeById(Integer userId, Integer codeId) throws IOException {
        if (verifyUserHasCode(userId, codeId)) codeDAO.deleteCodeById(codeId);
    }


    private String getFileContent(String path) {
        try {
            return new String(Files.readAllBytes(Path.of(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean verifyUserHasCode(Integer userId, Integer codeId) {
        return codeDAO.findCodesByUserId(userId).stream().anyMatch(i -> i.getId().equals(codeId));
    }

    private void createCodeFile(CreateCodeDTO createCodeDTO) throws IOException {
        Files.write(Path.of(createCodeDTO.getPath()), createCodeDTO.getCode().getBytes());
    }

}
