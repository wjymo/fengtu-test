package com.zzn.demo.vo;

import com.zzn.demo.util.ExcelField;
import lombok.Data;

@Data
public class RiskDeDeviceWarnVo {

	private String warnId;
	@ExcelField(title="车牌", align=2, sort=1)
	private String carplate;
	//	@ExcelField(title="imei", align=2, sort=2)
	private String imei;
	@ExcelField(title="所属机构", align=2, sort=2)
	private String orgName;
	@ExcelField(title="告警时间", align=2, sort=3)
	private String alarmTime;
//	@ExcelField(title="告警大类(聚合)", align=2, sort=4)
	private String alarmType;
	@ExcelField(title="告警类型", align=2, sort=6)
	private String alarmTypeName;
//	@ExcelField(title="告警小类(聚合)", align=2, sort=5)
	private String alarmSubType;
	@ExcelField(title="告警事件", align=2, sort=7)
	private String alarmSubTypeName;

	private String alarmLocation;
	@ExcelField(title="车速(km/h)", align=2, sort=10)
	private String speed;
	@ExcelField(title="驾驶人", align=2, sort=11)
	private String driverName;
	@ExcelField(title="联系电话", align=2, sort=12)
	private String driverPhone;
	private String address;
	@ExcelField(title="告警位置", align=2, sort=9)
	private String briefAddress;

	private String recentRisk;
	private String deviceType;
	private String deviceModel;
	private String existFlag;
	private String originFileName;
	private String fphotos;
	private String bPhotos;
	private Integer riskLevel ;
	private Integer alarmLevel ;
	@ExcelField(title="告警等级", align=2, sort=8)
	private String alarmLevelName ;
	private Double lng;
	private Double lat;

}
