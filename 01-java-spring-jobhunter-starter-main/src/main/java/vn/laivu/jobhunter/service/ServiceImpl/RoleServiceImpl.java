package vn.laivu.jobhunter.service.ServiceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.repository.PermissionRepository;
import vn.laivu.jobhunter.repository.RoleRepository;
import vn.laivu.jobhunter.service.RoleService;
import vn.laivu.jobhunter.unity.Permission;
import vn.laivu.jobhunter.unity.Role;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role create(Role role) {
        // check permission
        if(role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions()
                    .stream().map(permission -> permission.getId())
                    .collect(Collectors.toList());
            List<Permission> permissionsDB = this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(permissionsDB);
        }
        return this.roleRepository.save(role);
    }

    public Role fetchById(long id) {
        Optional<Role> optionalRole = this.roleRepository.findById(id);
        if (optionalRole.isPresent()) {
            return optionalRole.get();
        }
        return null;
    }

    public Role update(Role role) {
        Role roleInDB = this.fetchById(role.getId());
        //check permissions
        if(role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions()
                    .stream().map(permission -> permission.getId())
                    .collect(Collectors.toList());
            List<Permission> permissions = this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(permissions);
        }
        roleInDB.setName(role.getName());
        roleInDB.setDescription(role.getDescription());
        roleInDB.setActive(role.isActive());
        roleInDB.setPermissions(role.getPermissions());
        roleInDB = this.roleRepository.save(roleInDB);
        return roleInDB;
    }

    public void delete(long id) {
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDTO getRoles(Specification<Role> specification, Pageable pageable) {
        Page<Role> page = this.roleRepository.findAll(specification, pageable);
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
}
