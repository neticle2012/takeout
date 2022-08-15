package org.neticle.takeout.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Faruku123
 * @version 1.0
 * 文件上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
@Api(tags = "文件相关接口")
public class CommonController {
    @Value("${takeout.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file 该参数的名称必须与前端上传文件请求Form Data中的 name 一致（F12查看）
     */
    @ApiOperation(value = "文件上传接口")
    @ApiImplicitParam(name = "file", value = "文件对象", required = true)
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会被删除
        log.info(file.toString());
        String originalFilename = file.getOriginalFilename();//获取原始文件名
        //获取原始文件名的后缀（.jpeg, .png等）
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID随机生成文件名，防止因为文件名相同造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //判断当前目录是否存在，如果不存在则自动创建
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     */
    @ApiOperation(value = "文件下载接口")
    @ApiImplicitParam(name = "name", value = "文件名", required = true)
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        FileInputStream fis = null;
        ServletOutputStream sos = null;
        int len = 0;
        byte[] bytes = new byte[1024];
        response.setContentType("image/jpeg");
        try {
            //通过输入流读取文件内容（服务端硬盘 -> 服务端内存）
            fis = new FileInputStream(basePath + name);
            //通过输出流将文件写回浏览器，在浏览器展示图片（服务端内存 -> 网络通道）
            sos = response.getOutputStream();
            while ((len = fis.read(bytes)) != -1) {
                sos.write(bytes, 0, len);
                sos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {//关闭资源
            try {
                if (fis != null){
                    fis.close();
                }
                if (sos != null){
                    sos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
