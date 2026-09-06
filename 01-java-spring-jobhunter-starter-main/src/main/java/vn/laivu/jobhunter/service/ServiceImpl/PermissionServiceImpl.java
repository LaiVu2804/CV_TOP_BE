package vn.laivu.jobhunter.service.ServiceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.repository.PermissionRepository;
import vn.laivu.jobhunter.service.PermissionService;
import vn.laivu.jobhunter.unity.Permission;

import java.util.Optional;

@Service
public class PermissionServiceImpl implements PermissionService {

    private PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod());
    }

    public Permission create(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission fetchById(long id) {
        Optional<Permission> optionalPermission = this.permissionRepository.findById(id);
        if (optionalPermission.isPresent()) {
            return optionalPermission.get();
        }
        return null;
    }

    public Permission update(Permission p) {
        Permission permissionInDB = this.fetchById(p.getId());
        if (permissionInDB != null) {
            permissionInDB.setName(p.getName());
            permissionInDB.setApiPath(p.getApiPath());
            permissionInDB.setMethod(p.getMethod());
            permissionInDB.setModule(p.getModule());

            // update
            permissionInDB = this.permissionRepository.save(permissionInDB);
            return permissionInDB;
        }
        return null;
    }

    public void delete(long id) {
        // delete permission_role contain
        Optional<Permission> optionalPermission = this.permissionRepository.findById(id);
        Permission permission = optionalPermission.get();
        permission.getRoles().forEach(role -> role.getPermissions().remove(permission));
        // delete permission
        this.permissionRepository.delete(permission);
    }

    public ResultPaginationDTO getPermissions(Specification<Permission> specification, Pageable pageable) {
        Page<Permission> page = this.permissionRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(page.getContent());
        return rs;
    }

    public boolean isSameName(Permission p) {
        Permission permissionDB = this.fetchById(p.getId());

        if (permissionDB != null) {
            if (permissionDB.getName().equals(p.getName()))
                return true;
        }
        return false;
    }

}

