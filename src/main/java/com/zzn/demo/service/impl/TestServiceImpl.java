package com.zzn.demo.service.impl;

import com.zzn.demo.service.TestService;
import com.zzn.demo.vo.ReturnObj;
import com.zzn.demo.vo.TestQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TestServiceImpl implements TestService {
    public List<ReturnObj> getInt(TestQuery query){
        System.out.println("进入TestService"+query.getName()+": "+ Arrays.toString(query.getWifes().toArray()));
        int i = query.getInnerObj().getHeight() * query.getArg() * query.getWifes().size();
        List<ReturnObj> list=new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            ReturnObj returnObj = new ReturnObj();
            returnObj.setHeight(i*j+1);
            returnObj.setWight(query.getArg() * query.getWifes().size()+j);
            list.add(returnObj);
        }

        return list;
    }
}
