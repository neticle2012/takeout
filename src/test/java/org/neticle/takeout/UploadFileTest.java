package org.neticle.takeout;

import org.junit.jupiter.api.Test;

/**
 * @author Faruku123
 * @version 1.0
 */
public class UploadFileTest {
    @Test
    public void test1(){
        String fileName = "error.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }
}
