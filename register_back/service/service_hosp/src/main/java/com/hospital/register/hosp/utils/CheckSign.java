package com.hospital.register.hosp.utils;


import com.hospital.register.common.utils.MD5;
import com.hospital.register.hosp.service.HospitalSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * 用于判断医院签名是否一致的工具类
 * @author littlecheung
 */
@Component
public class CheckSign {

    @Autowired
    private HospitalSetService hospitalSetService;

    public static CheckSign checkSign;

    /**
     * "@PostConstruct":作用是在加载类的构造函数之后执行，也就是在加载了构造函数之后，执行init方法；
     */
    @PostConstruct
    public void init(){
        checkSign = this;
    }


    public static boolean checkSignEquals(Map<String, Object> paramMap){
        //获取医院编号
        String hoscode = (String) paramMap.get("hoscode");

        //获取医院系统传递过来的前面sign ,已经进行了MD5加密
        String hospSign = (String) paramMap.get("sign");

        //根据传递过来医院编号查询数据库，查询签名
        String signKey = checkSign.hospitalSetService.getSignKey(hoscode);

        //把数据库查询签名进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKey);

        return hospSign.equals(signKeyMd5);
    }
}
