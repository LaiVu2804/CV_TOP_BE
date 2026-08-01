package vn.ngotien.jobhunter.service.ServiceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngotien.jobhunter.domain.Company;
import vn.ngotien.jobhunter.domain.dto.Meta;
import vn.ngotien.jobhunter.domain.dto.ResultPaginationDTO;
import vn.ngotien.jobhunter.repository.CompanyRepository;
import vn.ngotien.jobhunter.service.CompanyService;

import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company createCom(Company com) {
        return companyRepository.save(com);
    }

    public ResultPaginationDTO getAllCom(Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(pageable);
        Meta meta = new Meta();
        meta.setPage(pageCompany.getTotalPages() -1);
        meta.setTotal(pageCompany.getTotalElements());
        meta.setPages(pageCompany.getTotalPages());
        meta.setPageSize(pageCompany.getTotalPages());

        ResultPaginationDTO rs = new ResultPaginationDTO();
        rs.setMeta(meta);
        rs.setResult(pageCompany.getContent());

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
        }).orElseThrow(() -> new IllegalArgumentException("không tìm thấy " + id));
    }

    public void deleteCom(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new IllegalArgumentException("không tìm thấy tai khoan " + id); //Ném thẳng lỗi ra message
        }
        companyRepository.deleteById(id);
    }

}
