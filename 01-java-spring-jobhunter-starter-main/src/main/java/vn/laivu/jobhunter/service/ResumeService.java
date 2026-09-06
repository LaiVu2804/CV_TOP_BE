package vn.laivu.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.laivu.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.laivu.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.laivu.jobhunter.unity.Job;
import vn.laivu.jobhunter.unity.Resume;
import vn.laivu.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@Service
public interface ResumeService {
    void checkResumeExistByUserAndJob(Resume resume) throws IdInvalidException;

    void checkResumeExistForUpdate(Resume resume, Resume reqResume) throws IdInvalidException;

    ResCreateResumeDTO createResume(Resume resume);

    ResUpdateResumeDTO updateResume(Resume resume);

    Optional<Resume> fetchById(long id);

    ResFetchResumeDTO getResume(Resume resume);

    void delete(long id);

    ResultPaginationDTO fetchResumeByUser(Pageable pageable);

    ResultPaginationDTO fetchAllResume(Specification<Resume> specification, Pageable pageable);
}
