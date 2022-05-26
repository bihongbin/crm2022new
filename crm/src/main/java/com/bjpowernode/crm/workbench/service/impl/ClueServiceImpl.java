package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtil;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.mapper.*;
import com.bjpowernode.crm.workbench.service.ClueService;
import org.apache.ibatis.annotations.Arg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author shkstart
 * @date 2022/5/21 - 10:20
 */
@Service("clueService")
public class ClueServiceImpl implements ClueService {
    @Autowired
    private ClueMapper clueMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ContactsMapper contactsMapper;

    @Autowired
    private ClueRemarkMapper clueRemarkMapper;

    @Autowired
    private CustomerRemarkMapper customerRemarkMapper;

    @Autowired
    private ContactsRemarkMapper contactsRemarkMapper ;

    @Autowired
    private ClueActivityRelationMapper clueActivityRelationMapper;

    @Autowired
    private ContactsActivityRelationMapper contactsActivityRelationMapper;

    @Autowired
    private TranMapper tranMapper;

    @Autowired
    private TranRemarkMapper tranRemarkMapper;


    @Override
    public int saveCreateClue(Clue clue) {
        int result = clueMapper.insertClue(clue);
        return result;
    }

    @Override
    public Clue queryClueForDetailById(String id) {
        Clue clue = clueMapper.selectClueForDetailById(id);
        return clue;
    }

    @Override
    public void saveConvertClue(Map<String, Object> map) {
        //得到clueId
        String clueId = (String) map.get("clueId");
        User user=(User) map.get(Contants.SESSION_USER);
        //是否生成交易
        String isCreateTran=(String) map.get("isCreateTran");
        //根据clueId查询线索信息
        Clue clue=clueMapper.selectClueById(clueId);
        //把该线索中有关公司的信息转换到客户表中
        Customer c=new Customer();
        c.setAddress(clue.getAddress());
        c.setContactSummary(clue.getContactSummary());
        c.setCreateBy(user.getId());
        c.setCreateTime(DateUtils.formateDateTime(new Date()));
        c.setDescription(clue.getDescription());
        c.setId(UUIDUtil.getUUID());
        c.setName(clue.getCompany());
        c.setNextContactTime(clue.getNextContactTime());
        c.setOwner(user.getId());
        c.setPhone(clue.getPhone());
        c.setWebsite(clue.getWebsite());
        customerMapper.insertCustomer(c);
        //把该线索中有关个人的信息转换到联系人表中
        Contacts co=new Contacts();
        co.setAddress(clue.getAddress());
        co.setAppellation(clue.getAppellation());
        co.setContactSummary(clue.getContactSummary());
        co.setCreateBy(user.getId());
        co.setCreateTime(DateUtils.formateDateTime(new Date()));
        co.setCustomerId(c.getId());
        co.setDescription(clue.getDescription());
        co.setEmail(clue.getEmail());
        co.setFullname(clue.getFullname());
        co.setId(UUIDUtil.getUUID());
        co.setJob(clue.getJob());
        co.setMphone(clue.getMphone());
        co.setNextContactTime(clue.getNextContactTime());
        co.setOwner(user.getId());
        co.setSource(clue.getSource());
        contactsMapper.insertContacts(co);

        //根据clueId查询该线索下所有的备注
        List<ClueRemark> crList=clueRemarkMapper.selectClueRemarkByClueId(clueId);

        //查看是否有备注，有就遍历线索备注，转换到客户备注表中
        if (crList != null && crList.size() > 0){
            //遍历crList，封装客户备注
            CustomerRemark cur=null;
            ContactsRemark cor=null;
            List<CustomerRemark> curList=new ArrayList<>();
            List<ContactsRemark> corList=new ArrayList<>();

            for (ClueRemark cr : crList) {
                //客户备注
                cur=new CustomerRemark();
                cur.setCreateBy(cr.getCreateBy());
                cur.setCreateTime(cr.getCreateTime());
                cur.setCustomerId(c.getId());
                cur.setEditBy(cr.getEditBy());
                cur.setEditFlag(cr.getEditFlag());
                cur.setEditTime(cr.getEditTime());
                cur.setId(UUIDUtil.getUUID());
                cur.setNoteContent(cr.getNoteContent());
                curList.add(cur);

                //联系人备注
                cor=new ContactsRemark();
                cor.setContactsId(co.getId());
                cor.setCreateBy(cr.getCreateBy());
                cor.setCreateTime(cr.getCreateTime());
                cor.setEditBy(cr.getEditBy());
                cor.setEditFlag(cr.getEditFlag());
                cor.setEditTime(cr.getEditTime());
                cor.setId(UUIDUtil.getUUID());
                cor.setNoteContent(cr.getNoteContent());
                corList.add(cor);

            }
            //调用service添加备注
            customerRemarkMapper.insertCustomerRemarkByList(curList);
            contactsRemarkMapper.insertContactsRemarkByList(corList);
        }

        //根据clueId查询该线索和市场活动的关联关系
        List<ClueActivityRelation> carList=clueActivityRelationMapper.selectClueActivityRelationByClueId(clueId);

        //把该线索和市场活动的关联关系转化到联系人和市场活动关联关系中
        if (carList != null && carList.size() > 0){
            //封装集合
            ContactsActivityRelation coar = null;
            List<ContactsActivityRelation> coarList = new ArrayList<>();
            //遍历
            for (ClueActivityRelation car : carList) {
                coar=new ContactsActivityRelation();
                coar.setActivityId(car.getActivityId());
                coar.setContactsId(co.getId());
                coar.setId(UUIDUtil.getUUID());
                coarList.add(coar);
            }
            //调用service方法添加联系人和市场活动关系
            contactsActivityRelationMapper.insertContactsActivityRelationByList(coarList);
        }

        //判断是否需要生成交易，真则往交易表添加一条交易记录，同时将线索备注转一份给交易备注信息
        if ("true".equals(isCreateTran)){

            Tran tran = new Tran();
            tran.setActivityId((String) map.get("activityId"));
            tran.setContactsId(co.getId());
            tran.setCreateBy(user.getId());
            tran.setCreateTime(DateUtils.formateDateTime(new Date()));
            tran.setCustomerId(c.getId());
            tran.setExpectedDate((String) map.get("expectedDate"));
            tran.setId(UUIDUtil.getUUID());
            tran.setMoney((String) map.get("money"));
            tran.setName((String) map.get("name"));
            tran.setOwner(user.getId());
            tran.setStage((String) map.get("stage"));
            tranMapper.insertTran(tran);

            if (crList != null && crList.size() > 0){
                //封装交易备注
                TranRemark tr=null;
                List<TranRemark> trList=new ArrayList<>();
                for (ClueRemark cr : crList) {
                    tr = new TranRemark();
                    tr.setCreateBy(cr.getCreateBy());
                    tr.setCreateTime(cr.getCreateTime());
                    tr.setEditBy(cr.getEditBy());
                    tr.setEditFlag(cr.getEditFlag());
                    tr.setEditTime(cr.getEditTime());
                    tr.setId(UUIDUtil.getUUID());
                    tr.setNoteContent(cr.getNoteContent());
                    tr.setTranId(tran.getId());
                    trList.add(tr);
                }

                //调用service方法插入交易备注
                tranRemarkMapper.insertTranRemarkByList(trList);
            }
        }

        //删除线索下的备注
        clueRemarkMapper.deleteClueRemarkByClueId(clueId);
        //删除线索和市场活动关联关系
        clueActivityRelationMapper.deleteClueActivityRelationByClueId(clueId);

        //删除线索
        clueMapper.deleteClueById(clueId);


    }
}
