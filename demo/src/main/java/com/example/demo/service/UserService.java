package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 服务类
 *
 * @author cwt
 * @date 2022/5/23 11:19
 */
public interface UserService {

    /**
     * 导入Excel表格数据
     * 
     * @param file
     * @return java.lang.Boolean
     * @author cwt
     * @date 2022/5/23
     */
    Boolean importExcel(MultipartFile file) throws IOException;
}
