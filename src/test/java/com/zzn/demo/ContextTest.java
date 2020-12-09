package com.zzn.demo;


import com.zzn.demo.controller.TestController;
import com.zzn.demo.service.impl.TestServiceImpl;
import com.zzn.demo.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class ContextTest {
    @Test
    public void testGetBean(){
        TestController bean = SpringContextUtil.getUniqueBean(TestController.class);
        TestServiceImpl testServiceImpl = (TestServiceImpl) bean.testService;
//        testService.getInt();
        System.out.println(1);
    }


}
