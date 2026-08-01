package vn.ngotien.jobhunter.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngotien.jobhunter.domain.Company;
import vn.ngotien.jobhunter.domain.ApiResponse;
import vn.ngotien.jobhunter.domain.dto.ResultPaginationDTO;
import vn.ngotien.jobhunter.service.CompanyService;


import java.util.Optional;

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

    @GetMapping("/get/companies")
    public ResponseEntity<ApiResponse<ResultPaginationDTO>> getAllCompanies(
            @RequestParam("current") Optional<String> currentOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {
        String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
        String sPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";

        int current = Integer.parseInt(sCurrent);
        int pageSize = Integer.parseInt(sPageSize);
        Pageable pageable = PageRequest.of(current - 1, pageSize);

        var result = new ApiResponse<>(HttpStatus.OK, "get all users success", companyService.getAllCom(pageable), null);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<Company>> getUserById(@PathVariable Long id) {
        return companyService.getComById(id).map(user -> {

            var response = new ApiResponse<Company>(HttpStatus.OK, "getUserById", user, null);

            return ResponseEntity.ok().body(response);

        }).orElseGet(() -> {
            ApiResponse<Company> adminResponse = new ApiResponse<>(HttpStatus.NOT_FOUND, "không tìm thấy user của id " + id, null, "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(adminResponse);
        });
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<ApiResponse<Company>> updateProduct(@PathVariable Long id, @RequestBody Company com) {

        Company updated = companyService.updateCom(id, com);

        var result = new ApiResponse<Company>(HttpStatus.OK, "Cập nhật thành công hehe", updated, null);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/user/{id}")
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

