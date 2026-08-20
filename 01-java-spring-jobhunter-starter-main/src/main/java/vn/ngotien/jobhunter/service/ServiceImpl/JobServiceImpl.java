package vn.ngotien.jobhunter.service.ServiceImpl;

import org.springframework.stereotype.Service;
import vn.ngotien.jobhunter.repository.JobRepository;
import vn.ngotien.jobhunter.service.JobService;
import vn.ngotien.jobhunter.unity.Job;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job createJob(Job job) {
        return jobRepository.save(job);
    }
}
