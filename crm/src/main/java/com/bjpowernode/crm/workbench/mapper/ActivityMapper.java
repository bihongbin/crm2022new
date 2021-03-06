package com.bjpowernode.crm.workbench.mapper;


import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Oct 22 08:57:21 CST 2020
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Oct 22 08:57:21 CST 2020
     */
    int insertSelective(Activity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Oct 22 08:57:21 CST 2020
     */
    Activity selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Oct 22 08:57:21 CST 2020
     */
    int updateByPrimaryKeySelective(Activity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Oct 22 08:57:21 CST 2020
     */
    int updateByPrimaryKey(Activity record);


    /**
     * 保存市场活动
     * @param activity
     * @return
     */
    int insertActivity(Activity activity);

    /**
     * 根据条件分页查询市场活动的列表
     * @param map
     * @return
     */
    List<Activity> selectActivityByConditionForPage(Map<String, Object> map);

    /**
     * 根据条件查询活动的总条数
     * @param map
     * @return
     */
    int selectCountOfActivityByCondition(Map<String, Object> map);


    /**
     * 根据id批量删除市场活动
     * @param ids
     * @return
     */
    int deleteActivityByIds(String[] ids);

    /**
     * 根据id查询单条市场活动
     * @param id
     * @return
     */
    Activity selectActivityById(String id);

    /**
     * 保存修改的活动
     * @param activity
     * @return
     */
    int updateActivity(Activity activity);

    /**
     * 查询所有活动
     * @return
     */
    List<Activity> selectAllActivitys();

    /**
     *批量保存创建的市场活动
     * @param activityList
     * @return
     */
    int insertActivityByList(List<Activity> activityList);

    /**
     *根据id查询市场活动详情信息
     * @param id
     * @return
     */
    Activity selectActivityForDetailById(String id);

    /**
     * 根据id查询与线索相关的市场活动
     * @param id
     * @return
     */

    List<Activity> selectActivityForDetailByClueId(String id);

    /**
     * 根据name条件模糊查询市场活动，并且排除与clueId关联过的活动
     * @param map
     * @return
     */
    List<Activity> selectActivityForDetailByNameClueId(Map<String, Object> map);

    /**
     * 根据ids查询市场活动的明细信息
     * @param ids
     * @return
     */
    List<Activity> selectActivityForDetailByIds(String[] ids);

    /**
     *根据name模糊查询市场活动并且查询那些与clueId关联过的活动
     * @param map
     * @return
     */
    List<Activity> selectActivityForConvertByNameClueId(Map<String, Object> map);
}