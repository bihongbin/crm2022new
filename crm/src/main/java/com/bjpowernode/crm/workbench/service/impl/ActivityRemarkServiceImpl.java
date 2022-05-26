package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.mapper.ActivityRemarkMapper;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("activityRemarkService")
public class ActivityRemarkServiceImpl implements ActivityRemarkService {

    @Autowired
    private ActivityRemarkMapper activityRemarkMapper;

    @Override
    public List<ActivityRemark> queryActivityRemarkForDetailByActivityId(String id) {
        List<ActivityRemark> activityRemarks = activityRemarkMapper.selectActivityRemarkForDetailByActivityId(id);
        return activityRemarks;
    }

    @Override
    public int saveCreateActivityRemark(ActivityRemark remark) {
        int result = activityRemarkMapper.insertActivityRemark(remark);

        return result;
    }

    @Override
    public int deleteActivityRemarkById(String id) {
        int result = activityRemarkMapper.deleteActivityRemarkById(id);
        return result;
    }

    @Override
    public int saveEditActivityRemark(ActivityRemark remark) {
        int result = activityRemarkMapper.updateActivityRemark(remark);
        return result;
    }
}
