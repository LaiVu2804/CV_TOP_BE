package vn.laivu.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.domain.response.user.ResUpdateDTO;
import vn.laivu.jobhunter.domain.response.user.RestCreateUserDTO;
import vn.laivu.jobhunter.unity.User;

@Service
public interface UserService {

    User handleCreateUser(User user);

    void handleDeleteUser(long id);

    ResUpdateDTO handleUpdateUser(ResUpdateDTO user);

    RestCreateUserDTO convertToRestCreateDTO(User user);

    User handleGetUserByUserName(String userName);

    boolean isEmailExist(String email);

    RestCreateUserDTO convertToRestDTO(User user);

    ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable);

    User fetchUserById(long id);

    void updateUserToken(String token, String email);

    User getUserByRefreshTokenAndEmail(String token, String email);
}
