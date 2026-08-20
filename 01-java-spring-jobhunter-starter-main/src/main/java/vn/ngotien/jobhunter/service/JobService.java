package vn.ngotien.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ngotien.jobhunter.domain.response.ResultPaginationDTO;
import vn.ngotien.jobhunter.unity.Job;

@Service
public interface JobService {
//    ResultPaginationDTO getAllJob(Specification<Job> spec, Pageable pageable);

    Job createJob(Job job);

//    Job updateJob(Long id, Job job);

//    void deleteJob(Long id);

    //chi dung optional cho doc du lieu
//    Optional<Company> getComById(Long id);
}
