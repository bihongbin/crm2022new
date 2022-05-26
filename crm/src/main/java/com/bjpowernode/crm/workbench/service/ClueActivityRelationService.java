package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.ClueActivityRelation;

import java.util.ArrayList;
import java.util.List;

public interface ClueActivityRelationService {

    int saveCreateClueActivityRelationByList(ArrayList<ClueActivityRelation> list);

    int deleteClueActivityRelationByClueIdActivityId(ClueActivityRelation relation);
}
