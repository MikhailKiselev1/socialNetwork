package org.javaproteam27.socialnetwork.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.aop.InfoLogger;
import org.javaproteam27.socialnetwork.model.dto.response.StatisticRs;
import org.javaproteam27.socialnetwork.service.StatisticService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@InfoLogger
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Tag(name = "statistics", description = "Взаимодействие с статистикой")
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping
    public StatisticRs getStatistics() {
        return statisticService.getStatistics();
    }
}
