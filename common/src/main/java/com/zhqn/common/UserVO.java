package com.zhqn.common;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserVO {

    private String name;

    private Date date;

    private LocalDate localDate;

    private LocalDateTime localDateTime;
}
