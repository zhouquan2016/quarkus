package com.zhqn.platform.mapper;

import com.zhqn.platform.domain.PersonEntity;

/**
* @author zhouquan3
* @description 针对表【person(用户)】的数据库操作Mapper
* @createDate 2022-11-17 18:37:59
* @Entity com.zhqn.paltform.domain.Person
*/
public interface PersonMapper {

    int deleteByPrimaryKey(Long id);

    int insert(PersonEntity record);

    int insertSelective(PersonEntity record);

    PersonEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PersonEntity record);

    int updateByPrimaryKey(PersonEntity record);

}
