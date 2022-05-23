package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.entity.dto.ImportExcelFile;
import com.example.demo.service.UserService;
import com.example.demo.util.ImportExcelUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Class Description
 *
 * @author cwt
 * @date 2022/5/23 11:21
 */
@Service
public class UserServiceImpl implements UserService {


    @Override
    public Boolean importExcel(MultipartFile file) throws IOException {
        //获取文件输入流
        InputStream is = file.getInputStream();
        //创建工作蒲用于读取文件信息
        ImportExcelUtil importExcel = new ImportExcelUtil();
        //调用工作蒲中的方法读取文件信息,此处使用stream流进行遍历插入操作
        List<Boolean> userList = importExcel.importDataFromExcel(is, file.getOriginalFilename()).stream().map(a -> {
            //原始类型为Object，进行类型强转
            ImportExcelFile userDto = (ImportExcelFile) a;
            User user = new User();
            user.setId(UUID.randomUUID().toString());
//            执行插入操作
//            userMapper.insert(user);
            System.out.println(a);
            return true;
        }).collect(Collectors.toList());

        return true;
    }

}

