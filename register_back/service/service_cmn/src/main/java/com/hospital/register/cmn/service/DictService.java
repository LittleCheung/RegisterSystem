package com.hospital.register.cmn.service;

import com.hospital.register.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {

    //根据数据id查询子数据列表
    List<Dict> findChildData(Long id);

    //导出到excel
    void exportData(HttpServletResponse response);

    //导入数据
    void importDictData(MultipartFile file);

    //根据dictcode和value进行查询
    String getDictName(String dictCode, String value);

    //根据dictCode获取下级结点
    List<Dict> findByDictCode(String dictCode);
}
