package com.hospital.register.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.hospital.register.oss.service.FileService;
import com.hospital.register.oss.utils.ConstantOssPropertiesUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    /**
     * 文件上传
     * @param file
     * @return
     */
    @Override
    public String upload(MultipartFile file) {

        //填写Bucket所在地域对应的Endpoint。
        String endpoint = ConstantOssPropertiesUtils.ENDPOINT;
        String accessKeyId = ConstantOssPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantOssPropertiesUtils.SECRET;
        String bucket = ConstantOssPropertiesUtils.BUCKET;

        //创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            //上传文件流
            InputStream inputStream = file.getInputStream();
            String filename = file.getOriginalFilename();

            //添加随机唯一值避免同名文件覆盖
            String uuid = UUID.randomUUID().toString().replaceAll("-","");
            filename= uuid + filename;

            //按照日期创建文件存储文件夹
            String timeUrl = new DateTime().toString("yyyy/MM/dd");
            filename = timeUrl + "/" + filename;

            // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt），Object完整路径中不能包含Bucket名称。
            ossClient.putObject(bucket, filename, inputStream);

            // 关闭OSSClient。
            ossClient.shutdown();
            //返回上传后文件路径
            String url = "https://" + bucket + "." + endpoint + "/" + filename;
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
