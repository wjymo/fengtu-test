package com.zzn.demo.util;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * @Author: pipan
 * @Description:
 * @Date: 2020/2/26 14:41
 * @Modify By:
 */
@Slf4j
public class HwObsRiskUtil {

    private String ak;
    private String sk;
    private String endpoint;

    private HwObsRiskUtil(String ak, String sk, String endpoint){
        this.ak=ak;
        this.sk=sk;
        this.endpoint=endpoint;
    }

    public static HwObsRiskUtil getInstance(String ak, String sk, String endpoint){
        return new HwObsRiskUtil(ak,sk,endpoint);
    }

    public String uploadFile(String path, String bucketname, MultipartFile file) throws Exception {
        String filename = System.currentTimeMillis()+file.getOriginalFilename();

        String date = DateFormatUtils.format(new Date(), "yyyyMMdd");
        String objKey = path +date+"/"+filename;
        // 创建ObsClient实例
        ObsClient obsClient = null;
        String tempUrl = "";
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            //ObsConfiguration config = new ObsConfiguration();
            //config.setAuthType(AuthTypeEnum.OBS);
           // config.setEndPoint(endpoint);
            // 创建ObsClient实例
            obsClient = new ObsClient(ak, sk, endpoint);
            PutObjectResult putObjectResult = obsClient.putObject(bucketname, objKey,inputStream);
            return objKey;
        } finally {
            if (obsClient != null) {
                try {
                    obsClient.close();
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }
    }

    public String uploadFile(String path,String bucketname,String fileName,InputStream file) throws Exception {

        String date = DateFormatUtils.format(new Date(), "yyyyMMdd");
        String objKey = path+fileName;
        // 创建ObsClient实例
        ObsClient obsClient = null;
        String tempUrl = "";
        try {
            // 创建ObsClient实例
            obsClient = new ObsClient(ak, sk, endpoint);
            PutObjectResult putObjectResult = obsClient.putObject(bucketname, objKey,file);
            return objKey;
        } finally {
            if (obsClient != null) {
                try {
                    obsClient.close();
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }
            if(file!=null){
                file.close();
            }
        }
    }

    /**
     * 生成临时对象访问url
     * @param bucket
     * @param objectname
     * @param expireSeconds
     * @return
     */
    public String getObjectUrl(String bucket, String objectname, long expireSeconds){
        // 创建ObsClient实例
        ObsClient obsClient = null;
        String tempUrl = "";
        try {
            ObsConfiguration config = new ObsConfiguration();
            config.setAuthType(AuthTypeEnum.OBS);
            config.setEndPoint(endpoint);
            // 创建ObsClient实例
            obsClient = new ObsClient(ak, sk, config);

            TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, expireSeconds);
            request.setBucketName(bucket);
            request.setObjectKey(objectname);

            TemporarySignatureResponse response = obsClient.createTemporarySignature(request);
            tempUrl = response.getSignedUrl();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (obsClient != null) {
                try {
                    obsClient.close();
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }
        return tempUrl;
    }

    public boolean deleteFile(String path, String bucketname) throws Exception {
        // 创建ObsClient实例
        ObsClient obsClient = null;
        try {
            // 创建ObsClient实例
            obsClient = new ObsClient(ak, sk, endpoint);
            DeleteObjectResult result = obsClient.deleteObject(bucketname, path);
            return result.isDeleteMarker();
        } finally {
            if (obsClient != null) {
                try {
                    obsClient.close();
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }
    }

    public InputStream downloadFile(String bucketName,String picName,String picPath){
        // 创建ObsClient实例
        // 读取对象内容
        File file = null;
        OutputStream os = null;
        InputStream input = null;
        ObsClient obsClient = new ObsClient(ak, sk, endpoint);
        try{
            //ObsObject obsObject = obsClient.getObject(bucketname, "20190816/864426033971401/1565926539849.csv");
            ObsObject obsObject = obsClient.getObject(bucketName,picPath);
            return obsObject.getObjectContent();

        }catch(Exception e){
            log.info("从OBS下载文件失败bucketName={},url={}",bucketName,picPath);
            return null;
        }finally {
            if(obsClient != null) {
                try {
                    obsClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean checkFileExist(String bucketName, String fileName){
        // 创建ObsClient实例
        ObsClient obsClient = null;
        String tempUrl = "";
        try {
            ObsConfiguration config = new ObsConfiguration();
            config.setAuthType(AuthTypeEnum.OBS);
            config.setEndPoint(endpoint);
            // 创建ObsClient实例
            obsClient = new ObsClient(ak, sk, config);
            return obsClient.doesObjectExist(bucketName,fileName);
        }catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (obsClient != null) {
                try {
                    obsClient.close();
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }
        return false;
    }

}
