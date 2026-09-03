package vn.laivu.jobhunter.service.ServiceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.laivu.jobhunter.domain.response.job.ResFetchJobDTO;
import vn.laivu.jobhunter.domain.response.job.TotalResponse;
import vn.laivu.jobhunter.repository.CompanyRepository;
import vn.laivu.jobhunter.repository.JobRepository;
import vn.laivu.jobhunter.repository.SkillRepository;
import vn.laivu.jobhunter.service.JobService;
import vn.laivu.jobhunter.unity.Company;
import vn.laivu.jobhunter.unity.Job;
import vn.laivu.jobhunter.unity.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public ResultPaginationDTO fetchAllJob(Specification<Job> spec, Pageable pageable, Job job) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setTotal(pageable.getPageNumber());
        meta.setPages(pageJob.getTotalPages());
        meta.setPageSize(pageJob.getTotalPages());

        ResultPaginationDTO rs = new ResultPaginationDTO();
        rs.setMeta(meta);
        rs.setResult(pageJob.getContent());

        List<ResFetchJobDTO> listJob = pageJob.getContent().stream().map(item -> {

            TotalResponse.JobCompanyDTO companyDto = item.getCompany() != null
                    ? new TotalResponse.JobCompanyDTO(
                    item.getCompany().getId(),
                    item.getCompany().getName())
                    : null;

            List<TotalResponse.JobSkillsDTO> skillDtoList = item.getSkills() != null
                    ? item.getSkills().stream()
                    .map(skill -> new TotalResponse.JobSkillsDTO(
                            skill.getId(),
                            skill.getName()))
                    .collect(Collectors.toList())
                    : new ArrayList<>(); // Nguyên tắc: List thì nên trả về rỗng [] chứ không trả null

            // 3. Khởi tạo và trả về DTO hoàn chỉnh
            return new ResFetchJobDTO(
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
                    skillDtoList,
                    companyDto
            );
        }).collect(Collectors.toList());

        rs.setResult(listJob);
        return rs;
    }

    public ResCreateJobDTO handleCreateJob(Job job) {
        // check skills
        if (job.getSkills() != null) {
            // Get list skill id - Long
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            // Get list skill by Id
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        // check company
        if (job.getCompany() != null) {
            Optional<Company> companyOptional = this.companyRepository.findById(job.getCompany().getId());
            if (companyOptional.isPresent()) {
                job.setCompany(companyOptional.get());
            }
        }
        // Create job
        Job currentJob = this.jobRepository.save(job);

        // Convert response
        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setLocation(currentJob.getLocation());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLevel(currentJob.getLevel());
        dto.setDescription(currentJob.getDescription());
        dto.setExperience(currentJob.getExperience());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.getIsActive());

        // Set skills for current job
        if (job.getSkills() != null) {
            List<TotalResponse.JobSkillsDTO> skills = currentJob.getSkills().stream().map(item ->
                            new TotalResponse.JobSkillsDTO(item.getId(), item.getName()))
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        TotalResponse.JobCompanyDTO companyResponse = Optional.ofNullable(currentJob.getCompany())
                .map(c -> {
                    TotalResponse.JobCompanyDTO cDto = new TotalResponse.JobCompanyDTO();
                    cDto.setId(c.getId());
                    cDto.setName(c.getName());
                    return cDto;
                })
                .orElse(null); // Nếu company null thì trả về null

        dto.setCompany(companyResponse);

        return dto;
    }

    public ResFetchJobDTO getJobById(long id) {
        return this.jobRepository.findById(id)
                .map(job -> {
                    // 1. Map Company (nếu có)
                    TotalResponse.JobCompanyDTO companyDTO = Optional.ofNullable(job.getCompany())
                            .map(c -> new TotalResponse.JobCompanyDTO(c.getId(), c.getName()))
                            .orElse(null);

                    // 2. Map List<Skill> sang List<SkillDTO> bằng Stream API
                    List<TotalResponse.JobSkillsDTO> skillDTOs = Optional.ofNullable(job.getSkills())
                            .map(skills -> skills.stream()
                                    .map(s -> new TotalResponse.JobSkillsDTO(s.getId(), s.getName()))
                                    .collect(Collectors.toList()))
                            .orElseGet(ArrayList::new);

                    // 3. Trả về DTO kết quả
                    return new ResFetchJobDTO(
                            job.getId(),
                            job.getName(),
                            job.getQuantity(),
                            job.getLevel(),
                            job.getLocation(),
                            job.getDescription(),
                            job.getSalary(),
                            job.getExperience(),
                            job.getStartDate(),
                            job.getEndDate(),
                            job.getIsActive(),
                            skillDTOs,
                            companyDTO
                    );
                }).orElseThrow(() -> new RuntimeException("Job với id = " + id + " không tồn tại"));
    }

    public ResCreateJobDTO handleUpdateJob(Job job) {
        // Get existing job from database to preserve createdAt and createdBy
        Job currentJob = this.jobRepository.findById(job.getId()).orElseThrow(() ->
                new RuntimeException("Job không tồn tại với id = " + job.getId()));

        // Update only the modifiable fields
        currentJob.setName(job.getName());
        currentJob.setLocation(job.getLocation());
        currentJob.setSalary(job.getSalary());
        currentJob.setQuantity(job.getQuantity());
        currentJob.setLevel(job.getLevel());
        currentJob.setExperience(job.getExperience());
        currentJob.setDescription(job.getDescription());
        currentJob.setStartDate(job.getStartDate());
        currentJob.setEndDate(job.getEndDate());
        currentJob.setIsActive(job.getIsActive());

        // check skills
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            currentJob.setSkills(dbSkills);
        }

        // check company
        if (job.getCompany() != null) {
            Optional<Company> opCompany = this.companyRepository.findById(job.getCompany().getId());
            if (opCompany.isPresent()) {
                currentJob.setCompany(opCompany.get());
            }
        }

        // update job (this will trigger @PreUpdate which sets updatedAt and updatedBy)
        currentJob = this.jobRepository.save(currentJob);

        // convert response
        ResCreateJobDTO dto = new ResCreateJobDTO();

        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setLocation(currentJob.getLocation());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLevel(currentJob.getLevel());
        dto.setExperience(currentJob.getExperience());
        dto.setDescription(currentJob.getDescription());

        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());

        if (job.getSkills() != null) {
            List<TotalResponse.JobSkillsDTO> skills = currentJob.getSkills().stream().map(item ->
                            new TotalResponse.JobSkillsDTO(item.getId(), item.getName()))
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        TotalResponse.JobCompanyDTO companyResponse = Optional.ofNullable(currentJob.getCompany())
                .map(c -> {
                    TotalResponse.JobCompanyDTO cDto = new TotalResponse.JobCompanyDTO();
                    cDto.setId(c.getId());
                    cDto.setName(c.getName());
                    return cDto;
                })
                .orElse(null); // Nếu company null thì trả về null

        dto.setCompany(companyResponse);

        return dto;
    }
}