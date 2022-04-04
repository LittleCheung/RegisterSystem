package com.hospital.register.cmn.controller;

import com.hospital.register.cmn.service.DictService;
import com.hospital.register.common.result.Result;
import com.hospital.register.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api("数据字典管理")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    /**
     * 根据id查询子数据列表
     * @param id
     * @return
     */
    @ApiOperation("根据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result<List<Dict>> findChildData(@PathVariable Long id){
        List<Dict> list = dictService.findChildData(id);
        return Result.ok(list);
    }


    /**
     * dict数据导出到excel
     * @param response
     */
    @ApiOperation("dict数据导出到excel")
    @GetMapping("exportData")
    public void exportData(HttpServletResponse response){
        dictService.exportData(response);
    }


    /**
     * 导入数据字典
     * @param file
     * @return
     */
    @ApiOperation("导入数据字典")
    @PostMapping("importData")
    public Result importDict(MultipartFile file){
        dictService.importDictData(file);
        return Result.ok();
    }


    /**
     * 根据dictcode和value查询
     * @param dictCode
     * @param value
     * @return
     */
    @ApiOperation("根据dictcode和value查询医院信息")
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode, @PathVariable String value){
        String dictName = dictService.getDictName(dictCode, value);
        return dictName;
    }


    /**
     * 根据value查询
     * @param value
     * @return
     */
    @ApiOperation("根据value查询医院信息")
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value){
        String dictName = dictService.getDictName(null, value);
        return dictName;
    }


    /**
     * 根据dictCode获取下级结点
     * @param dictCode
     * @return
     */
    @ApiOperation("")
    @GetMapping("findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode){
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }
}
