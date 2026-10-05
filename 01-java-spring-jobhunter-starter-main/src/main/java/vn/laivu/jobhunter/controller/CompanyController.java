package vn.laivu.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.laivu.jobhunter.unity.Company;
import vn.laivu.jobhunter.domain.response.ApiResponse;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.service.CompanyService;
import vn.laivu.jobhunter.util.Annotation.ApiMessage;
import vn.laivu.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/${api.version}")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createUser(@Valid @RequestBody Company postCompany) {
        Company com = this.companyService.createCom(postCompany);
        return ResponseEntity.status(HttpStatus.CREATED).body(com);
    }

    @GetMapping("/companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            @Filter Specification<Company> spec, Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.getAllCom(spec, pageable));
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("Fetch company by id")
    public ResponseEntity<Company> fetchById(@PathVariable("id") long id) throws IdInvalidException {
        Company company = this.companyService.handleGetCompanyById(id);
        if(company == null) {
            throw new IdInvalidException("Company ID: " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(company);
    }

    @PutMapping("/companies")
    @ApiMessage("Updated a company")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company reqCompany) throws IdInvalidException {
        Company company = this.companyService.handleGetCompanyById(reqCompany.getId());
        if(company == null) {
            throw new IdInvalidException("Company not found");
        }
        Company updatedCompany = this.companyService.handleUpdateCompany(reqCompany);
        return ResponseEntity.ok(updatedCompany);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        companyService.deleteCom(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST, ex.getMessage(), null, "Bad Request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

