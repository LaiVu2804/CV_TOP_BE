package vn.laivu.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.laivu.jobhunter.domain.response.job.ResFetchJobDTO;
import vn.laivu.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.laivu.jobhunter.service.JobService;
import vn.laivu.jobhunter.unity.Job;
import vn.laivu.jobhunter.util.Annotation.ApiMessage;
import vn.laivu.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/${api.version}")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Created a job")
    public ResponseEntity<ResCreateJobDTO> createNewJob(@Valid @RequestBody Job job) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleCreateJob(job));
    }

    @GetMapping("/jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJob(
            @Filter Specification<Job> spec, Pageable pageable, Job job) {

        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.fetchAllJob(spec, pageable, job));
    }

    @PutMapping("/jobs")
    @ApiMessage("Updated a job")
    public ResponseEntity<ResUpdateJobDTO> updateAJob(@Valid@RequestBody Job reqJob) throws IdInvalidException {
        Job currentJob = this.jobService.handleGetJobById(reqJob.getId());
        if(currentJob == null) {
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

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Deleted a job")
    public ResponseEntity<Void> deleteAJob(@PathVariable("id") long id) throws IdInvalidException {
        Job currentJob = this.jobService.handleGetJobById(id);
        if(currentJob == null) {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok().body(null);
    }
}
