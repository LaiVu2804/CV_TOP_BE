package vn.ngotien.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngotien.jobhunter.domain.response.ResultPaginationDTO;
import vn.ngotien.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.ngotien.jobhunter.domain.response.job.ResFetchJobDTO;
import vn.ngotien.jobhunter.service.JobService;
import vn.ngotien.jobhunter.unity.Job;
import vn.ngotien.jobhunter.util.Annotation.ApiMessage;
import vn.ngotien.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Created a job")
    public ResponseEntity<ResCreateJobDTO> createNewJob(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleCreateJob(job));
    }

    @GetMapping("/jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJob(
            @Filter Specification<Job> spec, Pageable pageable, Job job) {

        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.fetchAllJob(spec, pageable, job));
    }

    @PutMapping("jobs/{id}")
    @ApiMessage("Updated a job")
    public ResponseEntity<ResCreateJobDTO> updateJob(@Valid @RequestBody Job reqJob) throws IdInvalidException {
        ResCreateJobDTO currentJob = this.jobService.handleUpdateJob(reqJob);
        if (currentJob == null) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.handleUpdateJob(reqJob));
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get job by id")
    public ResponseEntity<ResFetchJobDTO> getJob(@PathVariable("id") long id) throws IdInvalidException {
        ResFetchJobDTO currentJob = this.jobService.getJobById(id);
        if (currentJob == null) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.ok().body(currentJob);
    }
}
