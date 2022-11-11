package com.zhqn.platform;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class FileServiceTest {

    @Test
    public void testUpload() {
        String body = given().multiPart("file", new File("D:\\dist\\swagger-codegen-3.0.34\\pom.docker.xml"))
                .param("fileName", "pom.docker.xml").post("/platform/upload").getBody().print();
        System.out.println(body);
    }
}
