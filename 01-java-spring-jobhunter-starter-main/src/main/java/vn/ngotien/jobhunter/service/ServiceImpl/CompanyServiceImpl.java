package vn.ngotien.jobhunter.service.ServiceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ngotien.jobhunter.unity.Company;
import vn.ngotien.jobhunter.domain.response.company.RestCompanyDTO;
import vn.ngotien.jobhunter.domain.response.ResultPaginationDTO;
import vn.ngotien.jobhunter.repository.CompanyRepository;
import vn.ngotien.jobhunter.service.CompanyService;

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
        meta.setTotal(pageable.getPageNumber());
        meta.setPages(pageCompany.getTotalPages());
        meta.setPageSize(pageCompany.getTotalPages());

        ResultPaginationDTO rs = new ResultPaginationDTO();
        rs.setMeta(meta);
        rs.setResult(pageCompany.getContent());

        List<RestCompanyDTO> listCompany = pageCompany.getContent().stream().map(item -> new RestCompanyDTO(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getLogo(),
                        item.getAddress(),
                        item.getCreateAt(),
                        item.getUpdatedAt()))
                .collect(Collectors.toList());

        rs.setResult(listCompany);
        return rs;
    }

    public Optional<Company> getComById(Long id) {
        return companyRepository.findById(id);
    }

    public Company updateCom(Long id, Company updateCom) {
        return companyRepository.findById(id).map(com -> {
            com.setDescription(updateCom.getDescription());
            com.setAddress(updateCom.getAddress());
            com.setName(updateCom.getName());
            com.setLogo(updateCom.getLogo());
            return companyRepository.save(com);
        }).orElseThrow(() -> new IllegalArgumentException("không tìm thấy id: " + id));
    }

    public void deleteCom(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new IllegalArgumentException("không tìm thấy id: " + id); //Ném thẳng lỗi ra message
        }
        companyRepository.deleteById(id);
    }

}
