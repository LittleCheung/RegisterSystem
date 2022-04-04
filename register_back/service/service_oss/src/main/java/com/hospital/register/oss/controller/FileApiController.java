package com.hospital.register.oss.controller;

import com.hospital.register.common.result.Result;
import com.hospital.register.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api("上传认证信息接口")
@RestController
@RequestMapping("/api/oss/file")
public class FileApiController {

    @Autowired
    private FileService fileService;

    /**
     * 上传文件到阿里云oss的接口
     * @return
     */
    @ApiOperation("上传文件接口")
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file){
        //获取上传文件
        String url = fileService.upload(file);
        return Result.ok(url);
    }
}
















