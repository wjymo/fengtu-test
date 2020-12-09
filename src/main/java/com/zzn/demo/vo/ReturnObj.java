package com.zzn.demo.vo;

import com.zzn.demo.util.ExcelField;
import lombok.Data;

@Data
public class ReturnObj {
    @ExcelField(title = "height")
    private Integer height;
    @ExcelField(title = "wight")
    private Integer wight;
}
