package vn.laivu.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.laivu.jobhunter.domain.response.job.ResFetchJobDTO;
import vn.laivu.jobhunter.unity.Job;

@Service
public interface JobService {
    ResultPaginationDTO fetchAllJob(Specification<Job> spec, Pageable pageable,Job job);

    ResCreateJobDTO handleCreateJob(Job job);

    ResCreateJobDTO handleUpdateJob(Job job);

//    Job handleGetJobById (long id);

    ResFetchJobDTO getJobById (long id);
}
