package vn.ngotien.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngotien.jobhunter.domain.response.ResultPaginationDTO;
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

    @GetMapping("/jobs")

    public ResponseEntity<ResultPaginationDTO> getAllJob(
            @Filter Specification<Job> spec, Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.fetchAllJob(spec, pageable));
    }
}
