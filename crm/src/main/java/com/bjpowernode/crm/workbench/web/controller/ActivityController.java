package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.HSSFUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtil;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @author shkstart
 * @date 2022/5/2 - 20:41
 */
@Controller
public class ActivityController {

    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ActivityRemarkService activityRemarkService;

    @RequestMapping(value = "/workbench/activity/index.do")
    public String activityIndex(HttpServletRequest request) {
        //查出所有用户，放到请求作用域中，带给网页
        List<User> users = userService.queryAllUser();

        request.setAttribute("userList", users);
        //转发到市场活动主页
        return "workbench/activity/index";
    }

    @RequestMapping(value = "/workbench/activity/saveCreateActivity.do")
    @ResponseBody
    public Object saveCreateActivity(Activity activity, HttpSession session) {

        //给activity补全属性
        activity.setCreateTime(DateUtils.formateDateTime(new Date()));
        activity.setId(UUIDUtil.getUUID());
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        activity.setCreateBy(user.getId());

        //创建返回对象
        ReturnObject returnObject = new ReturnObject();

        //调用service方法保存市场活动
        try {
            int result = activityService.saveCreateActivity(activity);

            if (result > 0) {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统繁忙，请稍后重试。。。");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统繁忙，请稍后重试。。。");

        }


        return returnObject;

    }


    @RequestMapping(value = "/workbench/activity/queryActivityByConditionForPage.do")
    @ResponseBody
    public Object queryActivityByConditionForPage(String name,String owner,String startDate,String endDate,
                                                  int pageNo,int pageSize){

        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("beginNo",(pageNo-1)*pageSize);
        map.put("pageSize",pageSize);

        //调用service方法查询数据
        List<Activity> activities = activityService.queryActivityByConditionForPage(map);
        int totalRows = activityService.queryCountOfActivityByCondition(map);

        //根据查询结果生成响应信息
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("activityList",activities);
        retMap.put("totalRows",totalRows);
        return retMap;
    }

    @RequestMapping(value = "/workbench/activity/deleteActivityIds.do")
    @ResponseBody
    public Object deleteActivityIds(String[] id){
        ReturnObject returnObject = new ReturnObject();

        try {
            //调用service方法删除活动
            int result = activityService.deleteActivityByIds(id);

            if (result > 0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统服务繁忙。。。");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统服务繁忙。。。");
        }
        return returnObject;
    }

    @RequestMapping(value = "/workbench/activity/queryActivityById.do")
    @ResponseBody
    public Object queryActivityById(String id){
        //调用service层方法，查询市场活动
        Activity activity = activityService.queryActivityById(id);
        //根据查询结果，返回响应信息
        return activity;
    }

    @RequestMapping(value = "/workbench/activity/saveEditActivity.do")
    public Object saveEditActivity(Activity activity,HttpSession session){

        User user = (User) session.getAttribute(Contants.SESSION_USER);
        //封装参数
        activity.setEditTime(DateUtils.formateDateTime(new Date()));
        activity.setCreateBy(user.getId());

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，保存修改的市场活动
            int ret = activityService.saveEditActivity(activity);

            if (ret > 0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统繁忙。。。");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统繁忙。。。");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/activity/exportAllActivitys.do")
   public void exportAllActivitys(HttpServletResponse response) throws IOException {
        //查询出所有活动
        List<Activity> activities = activityService.queryAllActivitys();
        //创建excl文件，把activities放到excl中
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("市场活动列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell=row.createCell(0);
        cell.setCellValue("ID");
        cell=row.createCell(1);
        cell.setCellValue("所有者");
        cell=row.createCell(2);
        cell.setCellValue("名称");
        cell=row.createCell(3);
        cell.setCellValue("开始日期");
        cell=row.createCell(4);
        cell.setCellValue("结束日期");
        cell=row.createCell(5);
        cell.setCellValue("成本");
        cell=row.createCell(6);
        cell.setCellValue("描述");
        cell=row.createCell(7);
        cell.setCellValue("创建时间");
        cell=row.createCell(8);
        cell.setCellValue("创建者");
        cell=row.createCell(9);
        cell.setCellValue("修改时间");
        cell=row.createCell(10);
        cell.setCellValue("修改者");

        //遍历activityList，创建HSSFRow对象，生成所有的数据行
        if (activities != null && activities.size() > 0){
            for (int i = 0; i < activities.size(); i++) {
                Activity activity = activities.get(i);
                //每遍历出活动生成一行
                sheet.createRow(i+1);
                //生成11列，每列数据从活动中获取
                cell=row.createCell(0);
                cell.setCellValue(activity.getId());
                cell=row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell=row.createCell(2);
                cell.setCellValue(activity.getName());
                cell=row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell=row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell=row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell=row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell=row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell=row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell=row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell=row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }
        }

        //把生成的excl文件下载到客户端
        //设置浏览器用哪个编码解析器
        response.setContentType("application/octet-stream;charset=UTF-8");
        //添加响应头
        //浏览器接收到响应信息之后，默认情况下，直接在显示窗口中打开响应信息；即使打不开，也会调用应用程序打开；只有实在打不开，才会激活文件下载窗口。
        //可以设置响应头信息，使浏览器接收到响应信息之后，直接激活文件下载窗口，即使能打开也不打开
        response.addHeader("Content-Disposition","attachment;filename=activityList.xls");
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);

        workbook.close();

        outputStream.flush();

    }

    /**
     * 配置springMvc文件上传解析器
     * @return
     */
    @RequestMapping("/workbench/activity/fileUpload.do")
    public @ResponseBody Object fileUpload(String userName, MultipartFile myFile) throws Exception{
        //把文本数据打印到控制台
        System.out.println("userName="+userName);
        //把文件在服务指定的目录中生成一个同样的文件
        String originalFilename=myFile.getOriginalFilename();
        File file=new File("D:\\course\\18-CRM\\阶段资料\\serverDir\\",originalFilename);//路径必须手动创建好，文件如果不存在，会自动创建
        myFile.transferTo(file);

        //返回响应信息
        ReturnObject returnObject=new ReturnObject();
        returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
        returnObject.setMessage("上传成功");
        return returnObject;
    }

    @RequestMapping("/workbench/activity/importActivity.do")
    @ResponseBody
    public Object importActivity(String userName,MultipartFile activityFile,HttpSession session) throws IOException {
        System.out.println("userName="+userName);

        //获取当前用户
        User user = (User) session.getAttribute(Contants.SESSION_USER);

        //新建返回对象
        ReturnObject returnObject = new ReturnObject();

        try {
            //获取文件流
            InputStream inputStream = activityFile.getInputStream();
            //以流来创建excel文件
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = workbook.getSheetAt(0);
            //最后一行的下标
            int lastRowNum = sheet.getLastRowNum();
            //新建list用来封装活动
            List<Activity> activityList = new ArrayList<>();
            //遍历每一行
            HSSFRow row = null;
            Activity activity = null;
            HSSFCell cell = null;
            for (int i = 1; i <= lastRowNum; i++) {
                row = sheet.getRow(i);
                activity = new Activity();
                activity.setId(UUIDUtil.getUUID());
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtils.formateDateTime(new Date()));
                activity.setCreateBy(user.getId());

                //遍历每一列
                //获取每列长度,,//row.getLastCellNum():最后一列的下标+1
                short num = row.getLastCellNum();
                for (int j = 0; j < num; j++) {
                    //根据row获取HSSFCell对象，封装了一列的所有信息
                    cell=row.getCell(j);//列的下标，下标从0开始，依次增加

                    //获取列中的数据
                    String cellValue = HSSFUtils.getCellValueForStr(cell);
                    if(j==0){
                        activity.setName(cellValue);
                    }else if(j==1){
                        activity.setStartDate(cellValue);
                    }else if(j==2){
                        activity.setEndDate(cellValue);
                    }else if(j==3){
                        activity.setCost(cellValue);
                    }else if(j==4){
                        activity.setDescription(cellValue);
                    }
                }
                //每一行所有列封装完之后把每个活动封装到list中
                activityList.add(activity);


            }
            //调用service方法把活动添加到表中
            int result = activityService.saveCreateActivityByList(activityList);

            returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            returnObject.setRetData(result);
        } catch (IOException e) {
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }
        return  returnObject;
    }

    @RequestMapping("/workbench/activity/detailActivity.do")
    public String detailActivity(String id,HttpServletRequest request){

        //调用service方法查询活动详情
        Activity activity = activityService.queryActivityForDetailById(id);

        //调用service方法查询活动备注
        List<ActivityRemark> remarkList = activityRemarkService.queryActivityRemarkForDetailByActivityId(id);

        //将数据保存到作用域中
        request.setAttribute("activity",activity);
        request.setAttribute("remarkList",remarkList);

        //请求转发
        return "workbench/activity/detail";
    }









}
