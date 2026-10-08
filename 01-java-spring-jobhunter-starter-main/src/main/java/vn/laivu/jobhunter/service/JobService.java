package vn.laivu.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.laivu.jobhunter.domain.response.job.ResFetchJobDTO;
import vn.laivu.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.laivu.jobhunter.unity.Job;

import vn.laivu.jobhunter.util.error.IdInvalidException;

@Service
public interface JobService {
    ResultPaginationDTO fetchAllJob(Specification<Job> spec, Pageable pageable,Job job);

    ResCreateJobDTO handleCreateJob(Job job) throws IdInvalidException;

    ResUpdateJobDTO handleUpdateJob(Job job) throws IdInvalidException;

    ResFetchJobDTO getJobById (long id);

    Job handleGetJobById(long id);

    void handleDeleteJob(long id);
}
