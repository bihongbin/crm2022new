package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.mapper.ActivityMapper;
import com.bjpowernode.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author shkstart
 * @date 2022/5/3 - 15:25
 */
@Service("activityService")
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public int saveCreateActivity(Activity activity) {
        int result = activityMapper.insertActivity(activity);
        return result;
    }

    @Override
    public List<Activity> queryActivityByConditionForPage(Map<String, Object> map) {
        List<Activity> activities = activityMapper.selectActivityByConditionForPage(map);
        return activities;
    }

    @Override
    public int queryCountOfActivityByCondition(Map<String, Object> map) {
        int num = activityMapper.selectCountOfActivityByCondition(map);
        return num;
    }

    @Override
    public int deleteActivityByIds(String[] ids) {
        int result = activityMapper.deleteActivityByIds(ids);
        return result;
    }

    @Override
    public Activity queryActivityById(String id) {
        Activity activity = activityMapper.selectActivityById(id);
        return activity;
    }

    @Override
    public int saveEditActivity(Activity activity) {

        int result = activityMapper.updateActivity(activity);

        return result;
    }

    @Override
    public List<Activity> queryAllActivitys() {
        List<Activity> activities = activityMapper.selectAllActivitys();
        return null;
    }

    @Override
    public int saveCreateActivityByList(List<Activity> activityList) {
        int result = activityMapper.insertActivityByList(activityList);

        return result;
    }

    @Override
    public Activity queryActivityForDetailById(String id) {
        Activity activity = activityMapper.selectActivityForDetailById(id);
        return activity;
    }

    @Override
    public List<Activity> queryActivityForDetailByClueId(String id) {
        List<Activity> activities = activityMapper.selectActivityForDetailByClueId(id);
        return activities;
    }

    @Override
    public List<Activity> queryActivityForDetailByNameClueId(Map<String, Object> map) {
        List<Activity> activityList = activityMapper.selectActivityForDetailByNameClueId(map);
        return activityList;
    }

    @Override
    public List<Activity> queryActivityForDetailByIds(String[] ids) {
        List<Activity> activities = activityMapper.selectActivityForDetailByIds(ids);
        return activities;
    }

    @Override
    public List<Activity> queryActivityForConvertByNameClueId(Map<String, Object> map) {
        List<Activity> activities = activityMapper.selectActivityForConvertByNameClueId(map);
        return activities;
    }
}
