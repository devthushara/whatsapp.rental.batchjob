package com.zoomigo.whatsapp.batchmailsender.web;

import com.zoomigo.whatsapp.batchmailsender.config.BatchProperties;
import com.zoomigo.whatsapp.batchmailsender.job.JobEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final BatchProperties props;
    private final JobEngine engine;
    public JobController(BatchProperties props, JobEngine engine){this.props = props; this.engine = engine;}

    @PostMapping("/run/{name}")
    public ResponseEntity<String> run(@PathVariable String name){ 
        var job = props.getJobs().stream().filter(j -> name.equals(j.getName())).findFirst().orElse(null);
        if (job == null) return ResponseEntity.notFound().build();
        engine.runJob(job);
        return ResponseEntity.ok("Triggered " + name);
    }
}
