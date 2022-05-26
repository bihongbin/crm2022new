package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtil;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.mapper.DicValueMapper;
import com.bjpowernode.crm.settings.service.DicValueService;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.ClueActivityRelation;
import com.bjpowernode.crm.workbench.domain.ClueRemark;
import com.bjpowernode.crm.workbench.mapper.ClueRemarkMapper;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueActivityRelationService;
import com.bjpowernode.crm.workbench.service.ClueRemarkService;
import com.bjpowernode.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author shkstart
 * @date 2022/5/21 - 10:21
 */
@Controller
public class ClueController {

    @Autowired
    private UserService userService;

    @Autowired
    private DicValueService dicValueService;

    @Autowired
    private ClueService clueService;

    @Autowired
    private ClueRemarkService clueRemarkService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ClueActivityRelationService clueActivityRelationService;


    @RequestMapping(value = "/workbench/clue/index.do")
    public String index(HttpServletRequest request){

        //调用service查询动态数据
        List<User> userList=userService.queryAllUsers();
        List<DicValue> appellationList=dicValueService.queryDicValueByTypeCode("appellation");
        List<DicValue> clueStateList=dicValueService.queryDicValueByTypeCode("clueState");
        List<DicValue> sourceList=dicValueService.queryDicValueByTypeCode("source");

        //把数据保存到request中
        request.setAttribute("userList",userList);
        request.setAttribute("appellationList",appellationList);
        request.setAttribute("clueStateList",clueStateList);
        request.setAttribute("sourceList",sourceList);

        //请求转发
        return "workbench/clue/index";
    }

    @RequestMapping(value = "/workbench/clue/saveCreateClue.do")
    @ResponseBody
    public Object saveCreateClue(Clue clue, HttpSession session){
        //获取当前用户
        User user=(User)session.getAttribute(Contants.SESSION_USER);
        //封装参数
        clue.setId(UUIDUtil.getUUID());
        clue.setCreateTime(DateUtils.formateDateTime(new Date()));
        clue.setCreateBy(user.getId());
        ReturnObject returnObject=new ReturnObject();
        try {
            //调用service层方法，保存创建的线索
            int ret = clueService.saveCreateClue(clue);

            if(ret>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else{
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试....");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }

        return returnObject;
    }


    @RequestMapping(value = "/workbench/clue/detailClue.do")
    public String detailClue(String id,HttpServletRequest request){
        //调用service层方法，查询数据
        Clue clue=clueService.queryClueForDetailById(id);
        List<ClueRemark> remarkList=clueRemarkService.queryClueRemarkForDetailByClueId(id);
        List<Activity> activityList=activityService.queryActivityForDetailByClueId(id);

        //把数据保存到request中
        request.setAttribute("clue",clue);
        request.setAttribute("remarkList",remarkList);
        request.setAttribute("activityList",activityList);

        //请求转发
        return "workbench/clue/detail";
    }
    @RequestMapping(value = "/workbench/clue/queryActivityForDetailByNameClueId.do")
    @ResponseBody
    public Object queryActivityForDetailByNameClueId(String activityName,String clueId){
        //封装参数(查询条件，用map封装)
        Map<String, Object> map = new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);

        //调用service层方法查询市场活动
        List<Activity> activityList = activityService.queryActivityForDetailByNameClueId(map);

        return activityList;
    }

    @RequestMapping(value = "/workbench/clue/saveBund.do")
    @ResponseBody
    public Object saveBund(String[] activityId,String clueId){
        //封装参数
        //封装集合
        ArrayList<ClueActivityRelation> list = new ArrayList<>();
        ClueActivityRelation car = null;
        for (String ai : activityId) {
            car = new ClueActivityRelation();
            car.setActivityId(ai);
            car.setClueId(clueId);
            car.setId(UUIDUtil.getUUID());
            list.add(car);
        }
        //新建返回对象
        ReturnObject returnObject = new ReturnObject();
        //调用service方法，批量保存线索和市场活动的关联关系
        int result = clueActivityRelationService.saveCreateClueActivityRelationByList(list);

        if (result > 0){
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            List<Activity> activityList = activityService.queryActivityForDetailByIds(activityId);
            returnObject.setRetData(activityList);
        }else {
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/clue/saveUnbund.do")
    @ResponseBody
    public Object saveUnbund(ClueActivityRelation relation){
        ReturnObject returnObject=new ReturnObject();
        try {
            //调用service层方法，删除线索和市场活动的关联关系
            int ret = clueActivityRelationService.deleteClueActivityRelationByClueIdActivityId(relation);

            if(ret>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else{
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试....");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }

        return returnObject;
    }

    @RequestMapping(value = "/workbench/clue/toConvert.do")
    public String toConvert(String id,HttpServletRequest request){

        //调用service层方法，查询线索的明细信息
        Clue clue=clueService.queryClueForDetailById(id);
        List<DicValue> stageList=dicValueService.queryDicValueByTypeCode("stage");

        //把数据保存到request中
        request.setAttribute("clue",clue);
        request.setAttribute("stageList",stageList);

        //请求转发
        return "workbench/clue/convert";
    }

    @RequestMapping(value = "/workbench/clue/queryActivityForConvertByNameClueId.do")
    @ResponseBody
    public Object queryActivityForConvertByNameClueId(String activityName,String clueId){
        //封装参数
        Map<String,Object> map=new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);
        //调用service方法查询市场活动
        List<Activity> activityList=activityService.queryActivityForConvertByNameClueId(map);

        return activityList;

    }

    @RequestMapping(value = "/workbench/clue/convertClue.do")
    @ResponseBody
    public Object convertClue(String clueId,String money,String name,String expectedDate,String stage,
                              String activityId,String isCreateTran,HttpSession session){
        //封装参数
        Map<String,Object> map=new HashMap<>();
        map.put("clueId",clueId);
        map.put("money",money);
        map.put("name",name);
        map.put("expectedDate",expectedDate);
        map.put("stage",stage);
        map.put("activityId",activityId);
        map.put("isCreateTran",isCreateTran);
        map.put(Contants.SESSION_USER,session.getAttribute(Contants.SESSION_USER));

        //返回对象
        ReturnObject returnObject = new ReturnObject();

        try {
            //调用service层方法，保存线索转换
            clueService.saveConvertClue(map);
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }
        return returnObject;
    }





}
