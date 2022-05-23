package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 控制层
 *
 * @author cwt
 * @date 2022/5/23 11:17
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private UserService userService;

    @PostMapping("/importExcelFile")
    public Boolean importExcel(MultipartFile file) throws IOException {
        return userService.importExcel(file);
    }
}
