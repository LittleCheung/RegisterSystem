package com.hospital.register.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.hospital.register.cmn.mapper.DictMapper;
import com.hospital.register.model.cmn.Dict;
import com.hospital.register.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

public class DictListener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;
    public DictListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }


    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        //调用方法，逐行读取存到数据库
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo, dict);
        dictMapper.insert(dict);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
