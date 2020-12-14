package com.template.logger.rest;

import com.template.logger.StatsService;
import com.template.logger.entities.Action;
import com.template.logger.service.LogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stats")
@PreAuthorize("hasAuthority('SUPER_ADMIN')")
public class LogController {

    private  final StatsService statsService;
    private final LogService logService;

    public LogController(StatsService statsService, LogService logService) {
        this.statsService = statsService;
        this.logService = logService;
    }

    @GetMapping("/visit")
    public Model getStats(Model model){
        model.addAttribute("Count", statsService.getRequestCount());
        model.addAttribute("StartedOn", statsService.getStartedOn());

        return model;
        //return "Request count: " + statsService.getRequestCount() + " started on: " + statsService.getStartedOn();
    }

    @GetMapping("/actions")
    public ResponseEntity<List<Action>> getActions(){
        return ResponseEntity.ok(logService.getAll());
    }
}
