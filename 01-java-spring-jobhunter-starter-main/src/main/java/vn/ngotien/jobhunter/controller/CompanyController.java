package vn.ngotien.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngotien.jobhunter.domain.Company;
import vn.ngotien.jobhunter.domain.ApiResponse;
import vn.ngotien.jobhunter.response.ResultPaginationDTO;
import vn.ngotien.jobhunter.service.CompanyService;

@RestController
@RequestMapping("/api/v1")
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
    public ResponseEntity<ApiResponse<Company>> getUserById(@PathVariable Long id) {
        return companyService.getComById(id).map(user -> {

            var response = new ApiResponse<>(HttpStatus.OK, "getUserById", user, null);

            return ResponseEntity.ok().body(response);

        }).orElseGet(() -> {
            ApiResponse<Company> adminResponse = new ApiResponse<>(HttpStatus.NOT_FOUND, "không tìm thấy user của id " + id, null, "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(adminResponse);
        });
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<ApiResponse<Company>> updateProduct(@PathVariable Long id, @RequestBody Company com) {

        Company updated = companyService.updateCom(id, com);

        var result = new ApiResponse<>(HttpStatus.OK, "Cập nhật thành công ", updated, null);

        return ResponseEntity.ok(result);
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

