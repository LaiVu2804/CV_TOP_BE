package vn.laivu.jobhunter.service.ServiceImpl;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.laivu.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.laivu.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.laivu.jobhunter.repository.JobRepository;
import vn.laivu.jobhunter.repository.ResumeRepository;
import vn.laivu.jobhunter.repository.UserRepository;
import vn.laivu.jobhunter.service.ResumeService;
import vn.laivu.jobhunter.unity.Job;
import vn.laivu.jobhunter.unity.Resume;
import vn.laivu.jobhunter.unity.User;
import vn.laivu.jobhunter.util.SecurityUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

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

    public ResCreateResumeDTO createResume(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        return res;
    }

    public Optional<Resume> fetchById(long id) {
        return this.resumeRepository.findById(id);
    }


    public ResUpdateResumeDTO updateResume(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        return res;
    }

    public void delete(long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResFetchResumeDTO getResume(Resume resume) {
        ResFetchResumeDTO res = new ResFetchResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreateBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        if (resume.getJob().getCompany() != null) {
            res.setCompanyName(resume.getJob().getCompany().getName());
        }

        res.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        res.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));
        return res;
    }

    public ResultPaginationDTO fetchAllResume(Specification<Resume> specification, Pageable pageable) {
        Page<Resume> page = this.resumeRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        // Get from frontend send request
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        // Get from dbs
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        resultPaginationDTO.setMeta(mt);

        // remove senstive data
        List<ResFetchResumeDTO> listResume = page.getContent()
                .stream().map(item -> this.getResume(item))
                .collect(Collectors.toList());

        resultPaginationDTO.setResult(listResume);
        return resultPaginationDTO;
    }

    // Tự Build query thay vì dùng @Filter <để hiểu bản chất>
    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        // query builder
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : null;
        FilterNode node = filterParser.parse("email='"+email+"'");
        FilterSpecification<Resume> specification = filterSpecificationConverter.convert(node);
        Page<Resume> page = this.resumeRepository.findAll(specification, pageable);

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        // Get from frontend send request
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        // Get from dbs
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        res.setMeta(mt);

        // remove senstive data
        List<ResFetchResumeDTO> listResume = page.getContent()
                .stream().map(item -> this.getResume(item))
                .collect(Collectors.toList());

        res.setResult(listResume);
        return res;
    }
}
