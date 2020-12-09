package com.zzn.demo.vo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@ToString
@Accessors(chain = true)
public class TestQuery {
    private Integer arg;
    private String name;
    private List<String> wifes;

    private InnerObj innerObj;

    private String result;
}
