package com.hospital.register.user.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.hospital.register.common.helper.JwtHelper;
import com.hospital.register.common.result.Result;
import com.hospital.register.model.user.UserInfo;
import com.hospital.register.user.service.UserInfoService;
import com.hospital.register.user.utils.ConstantWxPropertiesUtil;
import com.hospital.register.user.utils.HttpClientUtils;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Api("微信操作接口")
@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {

    @Autowired
    private UserInfoService userInfoService;


    /**
     * 生成微信二维码，获取微信二维码登录参数
     * @return
     */
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQuConnect(){
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put("appid", ConstantWxPropertiesUtil.WX_OPEN_APP_ID);
            map.put("scope","snsapi_login");
            //redirect_uri 这个参数需要编码
            String wxOpenRedirectUrl = ConstantWxPropertiesUtil.WX_OPEN_REDIRECT_URL;
            wxOpenRedirectUrl = URLEncoder.encode(wxOpenRedirectUrl, "utf-8");
            map.put("redirect_uri",wxOpenRedirectUrl);
            map.put("state", System.currentTimeMillis()+"");
            return Result.ok(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 微信二维码扫描后回调的方法，得到扫码人信息
     * @param code 二维码
     * @param state 状态
     * @return
     */
    @GetMapping("callback")
    public String callback(String code, String state){
        //获取临时票据code
        System.out.println("code : "+ code);
        //使用code和appid以及appscrect请求微信固定地址，得到access_token和openid
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtil.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtil.WX_OPEN_APP_SECRET,
                code);

        //使用httpClient进行请求
        try {
            String accesstokenInfo = HttpClientUtils.get(accessTokenUrl);
            System.out.println("accesstokenInfo: " + accesstokenInfo);

            //从返回字符串获取openid和access_token
            JSONObject jsonObject = JSONObject.parseObject(accesstokenInfo);
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");

            //根据openid判断数据库是否已经存在扫码人信息
            UserInfo userInfo =  userInfoService.selectWxInfoOpenId(openid);

            if(userInfo == null){
                //根据openid和access_token请求微信地址，得到扫描人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                String resultInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultInfo" + resultInfo);

                //获取用户信息
                JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);
                //解析用户信息：用户昵称与用户头像
                String nickname = resultUserInfoJson.getString("nickname");
                String headimgurl = resultUserInfoJson.getString("headimgurl");

                //获取扫描人信息添加数据库
                userInfo = new UserInfo();
                userInfo.setNickName(nickname);
                userInfo.setOpenid(openid);
                userInfo.setStatus(1);
                //保存到数据库
                userInfoService.save(userInfo);
            }

            //返回name和token字符串
            Map<String, Object> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);
            //判断userInfo是否有手机号，如果手机号为空返回openid，如果手机号不为空，返回openid值是空字符串
            //前端判断：如果openid不为空，绑定手机号，如果openid为空，不绑定手机号
            if(StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }
            //使用JWT生成token
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);

            //重定向到前端
            return "redirect:" + ConstantWxPropertiesUtil.YYGH_BASE_URL
                    + "/weixin/callback?token="+map.get("token")
                    +"&openid="+map.get("openid")+
                    "&name="+URLEncoder.encode((String)map.get("name"),"utf-8");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
