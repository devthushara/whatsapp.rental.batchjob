package com.zoomigo.whatsapp.batchmailsender.job;

import com.zoomigo.whatsapp.batchmailsender.config.BatchProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.Duration;

@Component
public class JobScheduler {
    private static final Logger log = LoggerFactory.getLogger(JobScheduler.class);

    private final BatchProperties batchProperties;
    private final JobEngine engine;
    private final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

    public JobScheduler(BatchProperties batchProperties, JobEngine engine) {
        this.batchProperties = batchProperties;
        this.engine = engine;
        scheduler.setPoolSize(4);
        scheduler.setThreadNamePrefix("job-sched-");
        scheduler.initialize();
    }

    @PostConstruct
    public void init() {
        if (batchProperties.getJobs() == null) return;
        for (BatchProperties.JobConfig cfg : batchProperties.getJobs()) {
            if (!cfg.isEnabled()) continue;
            if (cfg.getSchedule().getCron() != null && !cfg.getSchedule().getCron().isBlank()) {
                scheduler.schedule(() -> engine.runJob(cfg), new org.springframework.scheduling.support.CronTrigger(cfg.getSchedule().getCron()));
                log.info("Scheduled job {} cron={}", cfg.getName(), cfg.getSchedule().getCron());
            } else if (cfg.getSchedule().getFixedDelay() != null) {
                scheduler.scheduleWithFixedDelay(() -> engine.runJob(cfg), Duration.ofMillis(cfg.getSchedule().getFixedDelay()));
                log.info("Scheduled job {} fixedDelay={}", cfg.getName(), cfg.getSchedule().getFixedDelay());
            } else {
                log.info("Job {} has no schedule, will run manually", cfg.getName());
            }
        }
    }
}
