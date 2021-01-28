package com.template.config.scheduled;

import com.template.item.service.ItemService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
@Log4j2
public class DeleteEmptyItemsScheduledTask {

    private final ItemService itemService;

    public DeleteEmptyItemsScheduledTask(ItemService itemService) {
        this.itemService = itemService;
    }

    @Scheduled(cron = "0 0 0 ? * SUN")
    public void run() {
        LocalDateTime now = LocalDateTime.now();
        log.info("CRON JOB: Delete empty items BEGIN at: {}", now);
        itemService.deleteItemsBefore(now.minusDays(7));
        log.info("CRON JOB: Delete empty items END at: {}", LocalDateTime.now());
    }
}
