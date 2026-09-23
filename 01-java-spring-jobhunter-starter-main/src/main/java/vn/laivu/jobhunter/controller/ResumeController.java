package vn.laivu.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.laivu.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.laivu.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.laivu.jobhunter.service.ResumeService;
import vn.laivu.jobhunter.service.ServiceImpl.UserServiceImpl;
import vn.laivu.jobhunter.unity.Company;
import vn.laivu.jobhunter.unity.Job;
import vn.laivu.jobhunter.unity.Resume;
import vn.laivu.jobhunter.unity.User;
import vn.laivu.jobhunter.util.Annotation.ApiMessage;
import vn.laivu.jobhunter.util.SecurityUtil;
import vn.laivu.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/${api.version}")
public class ResumeController {

    @Autowired
    FilterBuilder filterBuilder;

    @Autowired
    FilterSpecificationConverter filterSpecificationConverter;

    private final ResumeService resumeService;
    private final UserServiceImpl userService;

    public ResumeController(ResumeService resumeService,
                            UserServiceImpl userService) {
        this.resumeService = resumeService;
        this.userService = userService;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume) throws IdInvalidException {
        // Check id exists
        this.resumeService.checkResumeExistByUserAndJob(resume);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.createResume(resume));
    }

    @PutMapping({"/resumes/{id}", "/resumes"})
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> update(
            @PathVariable(value = "id", required = false) Long id,
            @Valid @RequestBody Resume resume) throws IdInvalidException {

        if (id != null) {
            resume.setId(id);
        }
        if (resume.getId() <= 0) {
            throw new IdInvalidException("Resume không tìm thấy");
        }
        // check id exist
        Optional<Resume> optionalResume = this.resumeService.fetchById(resume.getId());
        if (optionalResume.isEmpty()) {
            throw new IdInvalidException("Resume không tìm thấy");
        }
        Resume reqResume = optionalResume.get();
        reqResume.setStatus(resume.getStatus());

        // check user & job
        this.resumeService.checkResumeExistForUpdate(resume, reqResume);

        if (resume.getUser() != null) {
            reqResume.setUser(resume.getUser());
        }
        if (resume.getJob() != null) {
            reqResume.setJob(resume.getJob());
        }

        return ResponseEntity.ok().body(this.resumeService.updateResume(reqResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> optionalResume = this.resumeService.fetchById(id);
        if (optionalResume.isEmpty()) {
            throw new IdInvalidException("Resume không tìm thấy");
        }
        this.resumeService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Fetch a resume by id")
    public ResponseEntity<ResFetchResumeDTO> getMethodName(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> optionalResume = this.resumeService.fetchById(id);
        if (optionalResume.isEmpty()) {
            throw new IdInvalidException("Resume không tìm thấy");
        }
        return ResponseEntity.ok().body(this.resumeService.getResume(optionalResume.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resume with paginate")
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            @Filter Specification<Resume> specification,
            Pageable pageable) {
        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : null;
        User currentUser = this.userService.handleGetUserByUserName(email);

        Specification<Resume> finalSpec = specification;

        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && !companyJobs.isEmpty()) {
                    arrJobIds = companyJobs
                            .stream()
                            .map(companyJob -> companyJob.getId())
                            .collect(Collectors.toList());
                }

                if (arrJobIds != null && !arrJobIds.isEmpty()) {
                    Specification<Resume> jobInSpec = filterSpecificationConverter.convert(
                            filterBuilder.field("job").in(filterBuilder.input(arrJobIds)).get());
                    finalSpec = jobInSpec.and(specification);
                } else {
                    finalSpec = (root, query, cb) -> cb.disjunction();
                }
            }
        }

        return ResponseEntity.ok().body(this.resumeService.fetchAllResume(finalSpec, pageable));
    }

    @GetMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }
}
