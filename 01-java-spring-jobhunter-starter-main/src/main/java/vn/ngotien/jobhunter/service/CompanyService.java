package vn.ngotien.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ngotien.jobhunter.unity.Company;

import vn.ngotien.jobhunter.domain.response.ResultPaginationDTO;

import java.util.Optional;

@Service
public interface CompanyService {
    ResultPaginationDTO getAllCom(Specification<Company> spec, Pageable pageable);

    Company createCom(Company company);

    Company updateCom(Long id, Company updateCom);

    void deleteCom(Long id);

    //chi dung optional cho doc du lieu
    Optional<Company> getComById(Long id);

    //Tim du lieu cho company
//    Optional<Company> findById(Long id);
}
