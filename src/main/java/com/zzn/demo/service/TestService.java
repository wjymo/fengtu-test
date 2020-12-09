package com.zzn.demo.service;

import com.zzn.demo.vo.ReturnObj;
import com.zzn.demo.vo.TestQuery;

import java.util.List;

public interface TestService {
    List<ReturnObj> getInt(TestQuery query);
}
