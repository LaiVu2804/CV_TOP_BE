package vn.laivu.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.unity.Company;

import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;

import java.util.Optional;

@Service
public interface CompanyService {
    ResultPaginationDTO getAllCom(Specification<Company> spec, Pageable pageable);

    Company createCom(Company company);

    Company updateCom(Long id, Company updateCom);

    void deleteCom(Long id);

    Company handleGetCompanyById(long id);
}
