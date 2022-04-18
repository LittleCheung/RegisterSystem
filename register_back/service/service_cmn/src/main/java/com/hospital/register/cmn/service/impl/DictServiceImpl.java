package com.hospital.register.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.hospital.register.cmn.listener.DictListener;
import com.hospital.register.cmn.mapper.DictMapper;
import com.hospital.register.cmn.service.DictService;
import com.hospital.register.model.cmn.Dict;
import com.hospital.register.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典管理
 * @author littlecheung
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    /**
     * 根据数据id获取子结点数据列表
     * @param id
     * @return
     */
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    @Override
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        for (Dict dict : dictList) {
            dict.setHasChildren( this.isChildren(dict.getId()) );
        }
        return dictList;
    }

    /**
     * 判断数据id下面是否有子结点
     * @param id
     * @return
     */
    private boolean isChildren(Long id){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(wrapper);
        return  count > 0;
    }

    /**
     * 将数据字典dict导出到excel
     * @param response
     */
    @Override
    public void exportData(HttpServletResponse response) {

        //设置下载信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = "dict";
        //设置响应头，以附件的的方式进行下载
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

        //查询数据库
        List<Dict> dictList = baseMapper.selectList(null);
        List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
        for(Dict dict : dictList) {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict, dictEeVo);
            dictVoList.add(dictEeVo);
        }

        try {
            //调用方法进行写操作
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("数据字典").doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 将excel文件导入数据字典
     * allEntries = true：方法调用后清空所有缓存
     * @param file
     */
    @CacheEvict(value = "dict", allEntries = true)
    @Override
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据上级编码与值获取数据字典名称
     * @param dictCode 上级编码
     * @param value 值
     * @return
     */
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    @Override
    public String getDictName(String dictCode, String value) {
        //如果dictCode为空，直接根据value查询
        if(StringUtils.isEmpty(dictCode)){
            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>().eq("value",value));
            if(dict == null) {
                return null;
            }
            return dict.getName();
        }else{
            //如果dictcode不为空，根据dictCode和value查询
            //先根据dictcode查询dict对象，得到dict的id值
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper.eq("dict_code", dictCode);
            Dict codeDict = baseMapper.selectOne(wrapper);
            Long parent_id = codeDict.getId();
            //然后根据parent_id和value进行查询
            Dict finalDict = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", parent_id)
                    .eq("value", value));
            return finalDict.getName();
        }
    }


    /**
     * 根据dictCode获取下级结点
     * @param dictCode
     * @return
     */
    @Override
    public List<Dict> findByDictCode(String dictCode) {

        //根据dictcode查询dict对象，得到dict的id值
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code", dictCode);
        Dict codeDict = baseMapper.selectOne(wrapper);
        Long parent_id = codeDict.getId();

        //根据id值获取子结点
        List<Dict> childData = this.findChildData(parent_id);
        return childData;
    }
}
