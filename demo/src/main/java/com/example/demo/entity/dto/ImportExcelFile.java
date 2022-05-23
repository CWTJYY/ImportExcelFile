package com.example.demo.entity.dto;

import lombok.Data;

/**
 * Excel表格接收实体
 *
 * @author cwt
 * @date 2022/5/23 11:25
 */
@Data
public class ImportExcelFile {

    /**
     * 姓名
     */
    private String name;

    /**
     * 账号（手机号码）
     */
    private String mobile;

    /**
     * 密码
     */
    private String password;
}
