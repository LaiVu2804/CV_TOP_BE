package vn.ngotien.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ngotien.jobhunter.service.JobService;
import vn.ngotien.jobhunter.unity.Job;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    public ResponseEntity<Job> createJob(@Valid @RequestBody Job postJob) {
        Job job = this.jobService.createJob(postJob);
        return ResponseEntity.status(HttpStatus.CREATED).body(job);
    }
}
