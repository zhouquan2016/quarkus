package com.zhqn.platform.dao;

import com.zhqn.platform.domain.PersonEntity;
import com.zhqn.platform.mapper.PersonMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PersonDao implements PersonMapper {

    @Inject
    SqlSessionFactory sessionFactory;


    @Override
    public int deleteByPrimaryKey(Long id) {
        SqlSession sqlSession = sessionFactory.openSession();
        try {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            int ret = personMapper.deleteByPrimaryKey(id);
            sqlSession.commit();
            return ret;
        }catch (Exception e) {
            sqlSession.rollback(true);
            throw e;
        }
    }

    @Override
    public int insert(PersonEntity record) {
        SqlSession sqlSession = sessionFactory.openSession();
        try {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            int ret = personMapper.insert(record);
            sqlSession.commit();
            return ret;
        }catch (Exception e) {
            sqlSession.rollback(true);
            throw e;
        }

    }

    @Override
    public int insertSelective(PersonEntity record) {
        SqlSession sqlSession = sessionFactory.openSession();
        try {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            int ret = personMapper.insertSelective(record);
            sqlSession.commit();
            return ret;
        }catch (Exception e) {
            sqlSession.rollback(true);
            throw e;
        }
    }

    @Override
    public PersonEntity selectByPrimaryKey(Long id) {
        SqlSession sqlSession = sessionFactory.openSession();
        PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
        return personMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(PersonEntity record) {
        SqlSession sqlSession = sessionFactory.openSession();
        try {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            int ret = personMapper.updateByPrimaryKeySelective(record);
            sqlSession.commit();
            return ret;
        }catch (Exception e) {
            sqlSession.rollback(true);
            throw e;
        }
    }

    @Override
    public int updateByPrimaryKey(PersonEntity record) {
        SqlSession sqlSession = sessionFactory.openSession();
        try {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            int ret = personMapper.updateByPrimaryKey(record);
            sqlSession.commit();
            return ret;
        }catch (Exception e) {
            sqlSession.rollback(true);
            throw e;
        }
    }
}
