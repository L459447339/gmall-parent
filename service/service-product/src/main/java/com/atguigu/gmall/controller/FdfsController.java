package com.atguigu.gmall.controller;

import com.atguigu.common.result.Result;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class FdfsController {

    @PostMapping("fileUpload")
    public Result fileUpload(@RequestParam("file") MultipartFile file){
        String url = "192.168.200.128:8080";
        String path = FdfsController.class.getClassLoader().getResource("tracker.conf").getPath();
        //加载fdfs配置信息
        try {
            ClientGlobal.init(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        //获取tracker连接
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer connection = null;
        try {
            connection = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //利用tracker连接使用storage
        StorageClient storageClient = new StorageClient(connection,null);
        //上传文件返回url
        String[] jpgs = new String[0];
        try {
            jpgs = storageClient.upload_appender_file(file.getBytes(), StringUtils.getFilenameExtension(file.getOriginalFilename()),null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        for (String jpg : jpgs) {
            url = url + "/" + jpg;
        }

        return Result.ok(url);
    }

}
