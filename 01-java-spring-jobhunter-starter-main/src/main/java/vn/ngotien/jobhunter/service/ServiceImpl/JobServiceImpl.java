package vn.ngotien.jobhunter.service.ServiceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ngotien.jobhunter.domain.response.ResultPaginationDTO;
import vn.ngotien.jobhunter.domain.response.job.RestJobDTO;
import vn.ngotien.jobhunter.domain.response.job.TotalResponse;
import vn.ngotien.jobhunter.repository.CompanyRepository;
import vn.ngotien.jobhunter.repository.JobRepository;
import vn.ngotien.jobhunter.repository.SkillRepository;
import vn.ngotien.jobhunter.service.JobService;
import vn.ngotien.jobhunter.unity.Job;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private final CompanyRepository companyRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;

    public JobServiceImpl(CompanyRepository companyRepository, SkillRepository skillRepository, JobRepository jobRepository) {
        this.companyRepository = companyRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
    }

    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    public ResultPaginationDTO fetchAllJob(Specification<Job> spec, Pageable pageable) {

        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageJob.getTotalPages());
        mt.setTotal(pageJob.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageJob.getContent());

        List<RestJobDTO> listJob = pageJob.getContent().stream().map(item -> new RestJobDTO(
                        item.getId(),
                        item.getName(),
                        item.getQuantity(),
                        item.getLevel(),
                        item.getLocation(),
                        item.getDescription(),
                        item.getSalary(),
                        item.getExperience(),
                        item.getStartDate(),
                        item.getEndDate(),
                        item.getIsActive(),
                        new TotalResponse.Job_Company(
                                item.getCompany() != null ? item.getCompany().getId() : 0,
                                item.getCompany() != null ? item.getCompany().getName() : null),
                        new TotalResponse.Job_Skill(
                                item.getCompany() != null ? item.getCompany().getId() : 0,
                                item.getCompany() != null ? item.getCompany().getName() : null)
                        ))
                .collect(Collectors.toList());

        rs.setResult(listJob);
        return rs;
    }
}
