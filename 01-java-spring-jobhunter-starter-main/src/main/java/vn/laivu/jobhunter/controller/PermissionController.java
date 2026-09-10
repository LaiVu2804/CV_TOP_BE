package vn.laivu.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.service.PermissionService;
import vn.laivu.jobhunter.unity.Permission;
import vn.laivu.jobhunter.util.Annotation.ApiMessage;
import vn.laivu.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/${api.version}")
public class PermissionController {

    private PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission p) throws IdInvalidException {
        // check permission exist?
        if (this.permissionService.isPermissionExist(p)) {
            throw new IdInvalidException("Permission đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(p));
    }

    @PutMapping("/permissions/{id}")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission p) throws IdInvalidException {
        // check ID exist?
        if (this.permissionService.fetchById(p.getId()) == null) {
            throw new IdInvalidException("Permission ID: " + p.getId() + " không tồn tại");
        }
        // check permission exist?
        if (this.permissionService.isPermissionExist(p)) {
            // check name trùng không cho phé
            if (this.permissionService.isSameName(p)) {
                throw new IdInvalidException("Permission đã tồn tại");
            }
        }
        // update permission
        return ResponseEntity.ok().body(this.permissionService.update(p));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check ID exist?
        if (this.permissionService.fetchById(id) == null) {
            throw new IdInvalidException("Permission ID: " + id + " không tồn tại");
        }
        // delete permission
        this.permissionService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch all permissions")
    public ResponseEntity<ResultPaginationDTO> getPermissions(
            @Filter Specification<Permission> specification, Pageable pageAble) {

        return ResponseEntity.ok(this.permissionService.getPermissions(specification, pageAble));
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Get permissions by id")
    public ResponseEntity<Permission> getRole(@PathVariable("id") long id) throws IdInvalidException {
        Permission permission = this.permissionService.fetchById(id);
        if(permission == null) {
            throw new IdInvalidException("Permission ID " + id + " not found");
        }
        return ResponseEntity.ok().body(permission);
    }
}
