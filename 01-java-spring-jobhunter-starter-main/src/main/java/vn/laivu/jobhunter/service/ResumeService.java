package vn.laivu.jobhunter.service;

import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.laivu.jobhunter.unity.Resume;

@Service
public interface ResumeService {
    boolean checkResumeExistByUserAndJob(Resume resume);

    ResCreateResumeDTO create(Resume resume);
}
