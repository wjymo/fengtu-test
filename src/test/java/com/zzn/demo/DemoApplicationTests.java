package com.zzn.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.DownloadFileRequest;
import com.obs.services.model.DownloadFileResult;
import com.obs.services.model.ObjectMetadata;
import com.zzn.demo.service.impl.TestServiceImpl;
import com.zzn.demo.util.ExportExcelByJson;
import com.zzn.demo.util.HwObsRiskUtil;
import com.zzn.demo.util.SpringContextUtil;
import com.zzn.demo.vo.InnerObj;
import com.zzn.demo.vo.TestQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
class DemoApplicationTests {
    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(0);
    private static final String NAME_PREFIX = "file-export-";
    private static final ThreadPoolExecutor EXECUTOR;
    private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();

    static {
        EXECUTOR = new ThreadPoolExecutor(10, 10,
                60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(200),
                r -> new Thread(r, NAME_PREFIX + THREAD_NUMBER.getAndIncrement()),
                (r, executor) -> { //r:请求执行的任务  executor:当前的线程池
                    //打印丢失的任务
                    log.error(r.toString() + " is discard");
                });
    }

    List<TestQuery> testQueries = new ArrayList<>();

    {
        for (int i = 0; i < 10; i++) {
            TestQuery testQuery = new TestQuery().setArg(i).setName("胡尧" + i)
                    .setWifes(Arrays.asList("王佩" + i, "方牙" + i, "骁儿" + i))
                    .setInnerObj(new InnerObj().setHeight(160 + i * 2));
            testQueries.add(testQuery);
        }
    }

    @Autowired
    private TestServiceImpl testServiceImpl;

    @Test
    void contextLoads() {
    }

    @Test
    public void testReflectionMethod() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        //字段需要被调用的service的全类名，service调用的方法的方法名，参数的全类名，
        // service返回值的全类名（很可能是泛型中的类型，因为返回list），
        //参数转为json字符串后的值，当前登录用户的用户名，用户的租户id
        String queryJson = "{\"name\":\"王佩\",\"arg\":2,\"wifes\":[\"骁儿\",\"胡尧\",\"庚庚\"],\"innerObj\":{\"height\":190}}";
        Class<?> queryClass = Class.forName("com.zzn.demo.vo.TestQuery");
        Class<?> serviceClass = Class.forName("com.zzn.demo.service.impl.TestServiceImpl");
        Object uniqueBean = SpringContextUtil.getUniqueBean(serviceClass);
        Method method = serviceClass.getDeclaredMethod("getInt", queryClass);
        Object o = JSON.parseObject(queryJson, queryClass);
        method.setAccessible(true);
        Object result = method.invoke(uniqueBean, o);
        Class<?> returnClass = Class.forName("com.zzn.demo.vo.ReturnObj");
//        returnClass result1 = (returnClass) result;
        System.out.println(result);
        String json = JSON.toJSONString(result);
        if (!json.startsWith("[") && json.startsWith("{")) {
            json = "[" + json + "]";
        }
        List<JSONObject> objects = JSON.parseObject(json, new TypeReference<List<JSONObject>>() {
        });
        //excel的titile存入数据库
        ExportExcelByJson exportExcel = new ExportExcelByJson("王佩标题", returnClass, 2);
        exportExcel = exportExcel.setDataJsonList(objects);
        String localPath = "D:\\项目资料\\智慧车辆安全监管平台\\导出下载中心\\mock-2.xlsx";
        exportExcel.write2stream(new FileOutputStream(localPath), null).dispose();

    }

    @Test
    public void testCompletableFutureWithExecutor() {
        //带线程池3秒
        CompletableFuture[] completableFutures = new CompletableFuture[testQueries.size()];
        long start = System.currentTimeMillis();
        for (int i = 0; i < testQueries.size(); i++) {
            TestQuery testQuery = testQueries.get(i);
            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                testQuery.setResult(testQuery.getName() + " love " + Arrays.toString(testQuery.getWifes().toArray()));
                return testQuery;
            }, EXECUTOR).thenApplyAsync((res) -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return res.setName(res.getName() + " 牛逼！！");
            }, EXECUTOR).thenAcceptAsync((res) -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(res);
            }, EXECUTOR);
            completableFutures[i] = future;
        }
        Void join = CompletableFuture.allOf(completableFutures).join();
        long end = System.currentTimeMillis();

        log.info("完成花费：{}毫秒", (end - start));
    }

    @Test
    public void testCompletableFutureWithExecutorAndException() {
        //带线程池3秒
        CompletableFuture[] completableFutures = new CompletableFuture[testQueries.size()];
        long start = System.currentTimeMillis();
        for (int i = 0; i < testQueries.size(); i++) {
            TestQuery testQuery = testQueries.get(i);
            CompletableFuture<TestQuery> testQueryCompletableFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                testQuery.setResult(testQuery.getName() + " love " + Arrays.toString(testQuery.getWifes().toArray()));
                return testQuery;
            }, EXECUTOR).handleAsync((res, throwable) -> {
                if (throwable != null) {
                    //System.out.println("throwable:"+throwable.getMessage());
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int i1 = 10 / 0;
                return res.setName(res.getName() + " 牛逼！！");
            }, EXECUTOR).handleAsync((res, throwable) -> {
                if (throwable != null) {
                    System.out.println("throwable:" + throwable.getMessage());
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(res);
                return res;
            }, EXECUTOR);
            completableFutures[i] = testQueryCompletableFuture;
        }
        Void join = CompletableFuture.allOf(completableFutures).join();
        long end = System.currentTimeMillis();

        log.info("完成花费：{}毫秒", (end - start));
    }

    @Test
    public void testCompletableFuture() {
        //带线程池5秒
        CompletableFuture[] completableFutures = new CompletableFuture[testQueries.size()];
        long start = System.currentTimeMillis();
        for (int i = 0; i < testQueries.size(); i++) {
            TestQuery testQuery = testQueries.get(i);
            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                testQuery.setResult(testQuery.getName() + " love " + Arrays.toString(testQuery.getWifes().toArray()));
                return testQuery;
            }).thenApplyAsync((res) -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return res.setName(res.getName() + " 牛逼！！");
            }).thenAcceptAsync((res) -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(res);
            });
            completableFutures[i] = future;
        }
        Void join = CompletableFuture.allOf(completableFutures).join();
        long end = System.currentTimeMillis();

        log.info("完成花费：{}毫秒", (end - start));
    }


    @Test
    public void testObs() {
//        HwObsRiskUtil obsRiskUtil = HwObsRiskUtil.getInstance("MPSF80YBVEL9LOTTNFDT",
//                "QLzZs029DUcoxLigw0wTU5nY5m0tawO7XM1UJ68A", "http://obs.cn-north-4.myhuaweicloud.com/");
        DownloadFileRequest request = new DownloadFileRequest("obs-app-risk-test",
                "testExport/20201207/6ae0e99e-c116-46d0-8df4-db61fbba670e/yzfkcs01-2020120414-2020120714.xlsx");
        // 设置下载对象的本地文件路径
        request.setDownloadFile("D:\\user\\Downloads\\xiaoer.xlsx");
        // 设置分段下载时的最大并发数
        request.setTaskNum(5);
        // 设置分段大小为10MB
        request.setPartSize(10 * 1024 * 1024);
        // 开启断点续传模式
        request.setEnableCheckpoint(true);
        ObsClient obsClient = new ObsClient("MPSF80YBVEL9LOTTNFDT",
                "QLzZs029DUcoxLigw0wTU5nY5m0tawO7XM1UJ68A", "http://obs.cn-north-4.myhuaweicloud.com/");
        try {
            // 进行断点续传下载
            DownloadFileResult result = obsClient.downloadFile(request);
            ObjectMetadata objectMetadata = result.getObjectMetadata();
            System.out.println(1);
        } catch (ObsException e) {
            // 发生异常时可再次调用断点续传下载接口进行重新下载
            e.printStackTrace();
        }
    }

    @Test
    public void testDate() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String str="2020-10";
        Date parse = simpleDateFormat.parse(str);
        System.out.println(parse);
    }
}
