package com.app.milobackend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class FilterDTO {
    private String word;
    private List<String> criteria;

}
