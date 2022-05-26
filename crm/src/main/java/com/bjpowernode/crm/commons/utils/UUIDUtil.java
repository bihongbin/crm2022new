package com.bjpowernode.crm.commons.utils;

import java.util.UUID;

/**
 * @author shkstart
 * @date 2022/5/3 - 15:20
 */
public class UUIDUtil {
    /**
     * 获取uuid作为id
     */
    public static String getUUID( ){
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        return  uuid;
    }
}
