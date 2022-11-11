package com.zhqn.platform;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

public class FileTest {

    @Test
    public void genFile() throws Exception {
        try(FileOutputStream os = new FileOutputStream("d:/test_gen.txt")) {
            for (int i = 1; i <= 1000000; i++) {
                for (int j = 0; j < 4; j++) {
                    byte w = (byte) (i >> ( (3 - j) * 8 ));

                    os.write(w);
                }
//                os.write(i);
            }
        }
    }

    @Test
    public void testFileSort() throws Exception {
        try (FileInputStream is = new FileInputStream("d:/test_gen.txt")){
//            is.read()
            int size;
            int buff[] = new int[4];
            int seq = 0;
            int ret;
            int pre = 0;
            while ((ret = is.read()) != -1) {
                buff[seq++] = ret;
                if (seq % 4 == 0) {
                    for (int i = 0; i < buff.length; i++) {
                        ret |= buff[i] << ( (3 - i) * 8 );
                    }
                    seq = 0;
                    System.out.println(ret);
                    assert ret > pre && ret - pre == 1;
                    pre = ret;
                }

            }
        }
    }
}
