package com.app.milobackend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Setter
@Getter
public class FilterDTO {
    private Map<String,String> Keys;
}
