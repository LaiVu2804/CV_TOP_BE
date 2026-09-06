package vn.laivu.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.unity.Permission;

@Service
public interface PermissionService {

    ResultPaginationDTO getPermissions(Specification<Permission> specification, Pageable pageable);

    Permission fetchById(long id);

    Permission update(Permission p);

    void delete(long id);

    boolean isSameName(Permission p);

    boolean isPermissionExist(Permission permission);

    Permission create(Permission permission);
}
