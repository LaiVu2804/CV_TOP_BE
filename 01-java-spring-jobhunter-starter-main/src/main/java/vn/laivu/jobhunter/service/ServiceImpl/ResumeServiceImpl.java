package vn.laivu.jobhunter.service.ServiceImpl;

import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.laivu.jobhunter.repository.JobRepository;
import vn.laivu.jobhunter.repository.ResumeRepository;
import vn.laivu.jobhunter.repository.UserRepository;
import vn.laivu.jobhunter.service.ResumeService;
import vn.laivu.jobhunter.unity.Job;
import vn.laivu.jobhunter.unity.Resume;
import vn.laivu.jobhunter.unity.User;

import java.util.Optional;

@Service
public class ResumeServiceImpl implements ResumeService {

    private ResumeRepository resumeRepository;
    private UserRepository userRepository;
    private JobRepository jobRepository;

    public ResumeServiceImpl(ResumeRepository resumeRepository, UserRepository userRepository,
                             JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        // check user by ID
        if (resume.getUser() == null) {
            return false;
        }
        Optional<User> optionalUser = this.userRepository.findById(resume.getUser().getId());
        if (optionalUser.isEmpty()) {
            return false;
        }

        // check job by id
        if (resume.getJob() == null) {
            return false;
        }
        Optional<Job> optionalJob = this.jobRepository.findById(resume.getJob().getId());
        if (optionalJob.isEmpty()) {
            return false;
        }

        return true;
    }

    public ResCreateResumeDTO create(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        return res;
    }
}
