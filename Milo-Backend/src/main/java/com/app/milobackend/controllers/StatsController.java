package com.app.milobackend.controllers;

import com.app.milobackend.dtos.StatsDTO;
import com.app.milobackend.services.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping
    public StatsDTO getStats() {
        return statsService.getStats();
    }
}
