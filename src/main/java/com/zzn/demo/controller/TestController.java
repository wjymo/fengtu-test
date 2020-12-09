package com.zzn.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zzn.demo.service.TestService;
import com.zzn.demo.service.impl.TestServiceImpl;
import com.zzn.demo.util.ExportExcelByJson;
import com.zzn.demo.util.HwObsRiskUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    public TestService testService;

    @GetMapping("/export")
    public String export(HttpServletResponse response) throws Exception {
        //json存入数据库
        String json = FileUtils.readFileToString(
                new File("D:\\项目资料\\智慧车辆安全监管平台\\导出下载中心\\mockRiskDeDeviceWarnVo.json")
                , "utf-8");
        //classPathStr存入数据库
        String classPathStr = "com.zzn.demo.vo.RiskDeDeviceWarnVo";
        Class<?> aClass = Class.forName(classPathStr);
        List<JSONObject> objects = JSON.parseObject(json, new TypeReference<List<JSONObject>>() {
        });
        //excel的titile存入数据库
        ExportExcelByJson exportExcel = new ExportExcelByJson("xx标题", aClass, 2);
        exportExcel = exportExcel.setDataJsonList(objects);
        String localPath = "D:\\项目资料\\智慧车辆安全监管平台\\导出下载中心\\mock.xlsx";
//        ExportExcelByJson dispose = exportExcel.write2stream(new FileOutputStream(localPath ),"mock.xlsx").dispose();
        String date = DateFormatUtils.format(new Date(), "yyyyMMdd");
        String uploadPath = "testExport/" + date + "/" + UUID.randomUUID().toString() + "/";
        HwObsRiskUtil obsRiskUtil = HwObsRiskUtil.getInstance("MPSF80YBVEL9LOTTNFDT",
                "QLzZs029DUcoxLigw0wTU5nY5m0tawO7XM1UJ68A", "http://obs.cn-north-4.myhuaweicloud.com/");
        String bucketname = "obs-app-risk-test";
        //objectKey存入数据库
//        String objectKey = obsRiskUtil.uploadFile(uploadPath,bucketname
//                , "mock-" + date + ".xlsx",
//                FileUtils.openInputStream(new File(localPath)));
        //"testExport/20201201/24b92264-74ee-495c-ad0b-22440c513edd/mock-20201201.xlsx"

        String objectKey = "testExport/20201201/57226211-46d5-4d84-8919-03a0e0d00aa6/mock-20201201.xlsx";
        InputStream inputStream = obsRiskUtil.downloadFile(
                bucketname, null, objectKey
        );
        BufferedInputStream bufferedInputStream=null;
        if (Objects.nonNull(inputStream)){
            try {
                String fileName = objectKey.substring(objectKey.lastIndexOf("/")+1, objectKey.length() - 1);
                response.reset();
                response.setContentType("application/octet-stream; charset=utf-8");
//                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
//        IOUtils.copy(inputStream, response.getWriter(), Charset.forName("GBK"));
                ServletOutputStream out = response.getOutputStream();
                bufferedInputStream=new BufferedInputStream(inputStream,1024);
                int len = 0;
                byte[] b = new byte[1024];
                while ((len = bufferedInputStream.read(b)) != -1) {
                    out.write(b, 0, len);
                }
            } finally {
                if(bufferedInputStream!=null){
                    bufferedInputStream.close();
                }
            }
        }
        return "success";
    }

}
