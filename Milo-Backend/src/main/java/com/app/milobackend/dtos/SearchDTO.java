package com.app.milobackend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class SearchDTO {
    private String word;
    private List<String> criteria;
}
