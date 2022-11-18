package com.zhqn.platform.configuration;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

@ApplicationScoped
@Slf4j
public class MybatisConfiguration {

    @Inject
    @DataSource("default")
    AgroalDataSource dataSource;

    @Produces
    public SqlSessionFactory sqlSessionFactory() throws IOException {
        log.info("initialing sqlSessionFactory...");
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("default", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMappers("com.zhqn.platform.mapper");
        configuration.addLoadedResource("mapper/*.xml");
        File file = Resources.getResourceAsFile("com/zhqn/platform/mapper/PersonMapper.xml");
        log.warn("file exists:{}", file);
//        configuration.addLoadedResource("mapper/PersonMapper.xml");
        configuration.getTypeAliasRegistry().registerAliases("com.zhqn.platform.domain");
        Collection<String> strings = configuration.getMappedStatementNames();
        if (strings == null || strings.isEmpty()) {
            log.warn("not found any mapper xml file!");
        }else {
            for (String mapperStatementId : strings) {
                System.out.println(mapperStatementId);
            }
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        log.info("initialing sqlSessionFactory success!");
        return sqlSessionFactory;
    }
}
