package com.zzn.demo.vo;

import lombok.Data;

@Data
public class ExportTask {
    private String serviceClassName;
    private String methodName;
    private String returnExportClassName;
    private String queryClassName;
    private String queryJsonArgs;
    /**
     * 任务状态，0：未开始，1：开始，2：成功，3：失败
     */
    private Integer taskStatus;
    private Long userId;
    private String tenantId;
}
