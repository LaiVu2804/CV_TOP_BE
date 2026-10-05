package vn.laivu.jobhunter.service.ServiceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.unity.Company;
import vn.laivu.jobhunter.domain.response.company.RestCompanyDTO;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.repository.CompanyRepository;
import vn.laivu.jobhunter.service.CompanyService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company createCom(Company com) {
        return companyRepository.save(com);
    }

    public ResultPaginationDTO getAllCom(Specification<Company> spec, Pageable pageable) {

        Page<Company> pageCompany = this.companyRepository.findAll(spec,pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setTotal(pageable.getPageSize());
        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        ResultPaginationDTO rs = new ResultPaginationDTO();
        rs.setMeta(meta);
        rs.setResult(pageCompany.getContent());

        List<RestCompanyDTO> listCompany = pageCompany.getContent().stream().map(item -> new RestCompanyDTO(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getLogo(),
                        item.getAddress(),
                        item.getCreatedAt(),
                        item.getUpdatedAt()))
                .collect(Collectors.toList());

        rs.setResult(listCompany);
        return rs;
    }

    public Company handleGetCompanyById(long id) {
        Optional<Company> optional = this.companyRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public Company handleUpdateCompany(Company company) {
        Optional<Company> companyOptional = this.companyRepository.findById(company.getId());
        if (companyOptional.isPresent()) {
            Company currentCompany = companyOptional.get();
            currentCompany.setName(company.getName());
            currentCompany.setDescription(company.getDescription());
            currentCompany.setAddress(company.getAddress());
            currentCompany.setLogo(company.getLogo());
            return this.companyRepository.save(currentCompany);
        }
        return null;
    }

    public void deleteCom(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new IllegalArgumentException("không tìm thấy id: " + id); //Ném thẳng lỗi ra message
        }
        companyRepository.deleteById(id);
    }

}
