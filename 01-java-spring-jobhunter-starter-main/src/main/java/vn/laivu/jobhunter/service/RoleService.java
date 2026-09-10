package vn.laivu.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.unity.Role;

@Service
public interface RoleService {

    ResultPaginationDTO getRoles(Specification<Role> specification, Pageable pageable);

    Role create(Role role);

    Role fetchById(long id);

    Role update(Role role);

    void delete(long id);

    boolean existByName(String name);
}
