package com.example.demo.entity;

import lombok.Data;

/**
 * 用户实体类
 *
 * @author cwt
 * @date 2022/5/23 11:15
 */
@Data
public class User {
    /**
     * id
     */
    private String id;

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
