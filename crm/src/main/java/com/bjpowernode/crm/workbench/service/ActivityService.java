package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

/**
 * @author shkstart
 * @date 2022/5/3 - 15:24
 */
public interface ActivityService {
    int saveCreateActivity(Activity activity);

    List<Activity> queryActivityByConditionForPage(Map<String,Object> map);

    int queryCountOfActivityByCondition(Map<String,Object> map);

    int deleteActivityByIds(String[] id);

    Activity queryActivityById(String id);

    int saveEditActivity(Activity activity);

    List<Activity> queryAllActivitys();

    int saveCreateActivityByList(List<Activity> activityList);

    Activity queryActivityForDetailById(String id);

    List<Activity> queryActivityForDetailByClueId(String id);

    List<Activity> queryActivityForDetailByNameClueId(Map<String, Object> map);

    List<Activity> queryActivityForDetailByIds(String[] activityId);

    List<Activity> queryActivityForConvertByNameClueId(Map<String, Object> map);
}
