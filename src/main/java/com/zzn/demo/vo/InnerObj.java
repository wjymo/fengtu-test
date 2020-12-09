package com.zzn.demo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class InnerObj {
    private Integer height;
}
