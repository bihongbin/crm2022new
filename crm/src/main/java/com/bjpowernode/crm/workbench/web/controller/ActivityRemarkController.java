package com.bjpowernode.crm.workbench.web.controller;

import com.alibaba.druid.sql.visitor.functions.If;
import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtil;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.mapper.ActivityRemarkMapper;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author shkstart
 * @date 2022/5/20 - 12:17
 */
@Controller
public class ActivityRemarkController {

    @Autowired
    private ActivityRemarkService activityRemarkService;

    @RequestMapping(value = "/workbench/activity/saveCreateActivityRemark.do")
    @ResponseBody
    public Object saveCreateActivityRemark(ActivityRemark remark, HttpSession session) {
        //获取当前用户
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        //封装参数
        remark.setId(UUIDUtil.getUUID());
        remark.setCreateTime(DateUtils.formateDateTime(new Date()));
        remark.setCreateBy(user.getId());
        remark.setEditFlag(Contants.REMARK_EDIT_FLAG_NO_EDITED);

        //相应对象
        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service方法保存市场活动备注
            int result = activityRemarkService.saveCreateActivityRemark(remark);
            if (result > 0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setRetData(remark);
            }else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试....");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }

        return returnObject;
    }

    @RequestMapping(value = "/workbench/activity/deleteActivityRemarkById.do")
    @ResponseBody
    public Object deleteActivityRemarkById(String id){
        //相应对象
        ReturnObject returnObject = new ReturnObject();

        try {
            //调用service方法删除市场活动备注
            int result = activityRemarkService.deleteActivityRemarkById(id);
            if (result > 0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试....");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }
        return  returnObject;
    }


    @RequestMapping(value = "/workbench/activity/saveEditActivityRemark.do")
    @ResponseBody
    public Object saveEditActivityRemark(ActivityRemark remark,HttpSession session){
        //获取当前用户
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        //相应对象
        ReturnObject returnObject = new ReturnObject();
        //封装参数
        remark.setEditTime(DateUtils.formateDateTime(new Date()));
        remark.setEditBy(user.getId());
        remark.setEditFlag(Contants.REMARK_EDIT_FLAG_YES_EDITED);


        try {
            //调用service方法，保存修改的活动备注
            int result = activityRemarkService.saveEditActivityRemark(remark);
            if (result > 0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setRetData(remark);
            }else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试....");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }


        return returnObject;
    }
}
