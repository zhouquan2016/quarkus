package com.zhqn.platform.domain;

import lombok.Data;

import java.time.LocalDate;

/**
 * 用户
 * @TableName person
 */
@Data
public class PersonEntity {
    /**
     * 主键
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

}