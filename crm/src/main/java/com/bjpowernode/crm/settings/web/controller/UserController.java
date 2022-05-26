package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shkstart
 * @date 2022/5/1 - 20:55
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService ;

    /**
     * url要和controller方法处理完请求之后，响应信息返回的页面路径目录保持一致
     * @return
     */
    @RequestMapping(value = "/settings/qx/user/toLogin.do")
    public String toLogin(){
        return "settings/qx/user/login";
    }

    @RequestMapping(value = "/settings/qx/user/login.do")
    @ResponseBody
    public Object login(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request, HttpServletResponse response, HttpSession session){
        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        //调用service方法查询用户
        User user = userService.queryUserByLoginActAndPwd(map);

        //根据查询结果，生成响应信息
        ReturnObject returnObject = new ReturnObject();
        if(user==null){
            //登录失败,用户名或者密码错误
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("用户名或者密码错误");
        }else{//进一步判断账号是否合法
            //user.getExpireTime()   //2019-10-20
            //        new Date()     //2020-09-10

            String nowStr = DateUtils.formateDateTime(new Date());
            if(nowStr.compareTo(user.getExpireTime())>0){
                //登录失败，账号已过期
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("账号已过期");
            }else if("0".equals(user.getLockState())){
                //登录失败，状态被锁定
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("状态被锁定");
            }else if(!user.getAllowIps().contains(request.getRemoteAddr())){
                //登录失败，ip受限
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("ip受限");
            }else{
                //登录成功
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);

                //将对象放到session域中
                session.setAttribute(Contants.SESSION_USER,user);

                //如果需要保存cookie，则往浏览器写cookie
                if ("true".equals(isRemPwd)){
                    Cookie cookieAct = new Cookie("loginAct", user.getLoginAct());
                    cookieAct.setMaxAge(60*60*24*10);
                    Cookie cookiePwd = new Cookie("loginPwd", user.getLoginPwd());
                    cookiePwd.setMaxAge(60*60*24*10);
                    response.addCookie(cookieAct);
                    response.addCookie(cookiePwd);
                }else {
                    //把没有过期的cookie删除，（覆盖旧cookie）
                    Cookie cookieAct = new Cookie("loginAct", "1");
                    cookieAct.setMaxAge(0);
                    Cookie cookiePwd = new Cookie("loginPwd", "1");
                    cookiePwd.setMaxAge(0);
                    response.addCookie(cookieAct);
                    response.addCookie(cookiePwd);

                }
            }
        }
        return returnObject;
    }


    @RequestMapping(value = "/settings/qx/user/logout.do")
    public String logout(HttpServletResponse response,HttpSession session){
        //清除cookie和session
        Cookie cookieAct = new Cookie("loginAct", "1");
        cookieAct.setMaxAge(0);
        Cookie cookiePwd = new Cookie("loginPwd", "1");
        cookiePwd.setMaxAge(0);
        response.addCookie(cookieAct);
        response.addCookie(cookiePwd);

        session.invalidate();

        //跳转到首页
        return "redirect:/";
    }
}
