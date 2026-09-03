package vn.laivu.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.job.TotalResponse;
import vn.laivu.jobhunter.unity.Company;
import vn.laivu.jobhunter.unity.User;
import vn.laivu.jobhunter.repository.CompanyRepository;
import vn.laivu.jobhunter.repository.UserRepository;
import vn.laivu.jobhunter.domain.response.user.ResUpdateDTO;
import vn.laivu.jobhunter.domain.response.user.RestCreateUserDTO;
import vn.laivu.jobhunter.domain.response.user.RestUserDTO;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public User handleCreateUser(User user) {
        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyRepository.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }
        return userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            Company com = companyOptional.get();
            List<User> users = this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(users);
        }
        this.userRepository.deleteById(id);
    }

    public ResUpdateDTO handleUpdateUser(ResUpdateDTO user) {

        User currentUser = this.userRepository.findById(user.getId()).orElseThrow(() ->
                new RuntimeException("User không tồn tại với id = " + user.getId()));

        // Update only the modifiable fields
        currentUser.setName(user.getName());
        currentUser.setAddress(user.getAddress());
        currentUser.setAge(user.getAge());
        currentUser.setUpdatedAt(user.getUpdatedAt());
        currentUser.setUpdatedBy(user.getUpdatedBy());

        // check company
        if (user.getCompany() != null) {
            Optional<Company> opCompany = this.companyRepository.findById(user.getCompany().getId());
            if (opCompany.isPresent()) {
                currentUser.setCompany(opCompany.get());
            }
        }

        currentUser = this.userRepository.save(currentUser);

        // convert response
        ResUpdateDTO dto = new ResUpdateDTO();

        dto.setId(user.getId());
        dto.setName(currentUser.getName());
        dto.setAddress(currentUser.getAddress());
        dto.setAge(currentUser.getAge());
        dto.setGender(currentUser.getGender());
        dto.setUpdatedAt(currentUser.getUpdatedAt());
        dto.setUpdatedBy(currentUser.getUpdatedBy());


        TotalResponse.CompanyUser companyResponse = Optional.ofNullable(currentUser.getCompany())
                .map(c -> {
                    TotalResponse.CompanyUser cDto = new TotalResponse.CompanyUser();
                    cDto.setId(c.getId());
                    cDto.setName(c.getName());
                    return cDto;
                })
                .orElse(null); // Nếu company null thì trả về null

        dto.setCompany(companyResponse);

        return dto;

    }

    public RestCreateUserDTO convertToRestCreateDTO(User user) {
        RestCreateUserDTO res = new RestCreateUserDTO();
        RestCreateUserDTO.Company_User company = new RestCreateUserDTO.Company_User();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        res.setAddress(user.getAddress());

        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }

        return res;
    }

    public User handleGetUserByUserName(String userName) {
        return this.userRepository.findByEmail(userName);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public RestCreateUserDTO convertToRestDTO(User user) {
        RestCreateUserDTO res = new RestCreateUserDTO();
        RestCreateUserDTO.Company_User company = new RestCreateUserDTO.Company_User();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        res.setAddress(user.getAddress());

        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }

        return res;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageUser.getContent());

        List<RestUserDTO> listUser = pageUser.getContent().stream().map(item -> new RestUserDTO(
                        item.getId(),
                        item.getName(),
                        item.getEmail(),
                        item.getGender(),
                        item.getAddress(),
                        item.getAge(),
                        item.getUpdatedAt(),
                        item.getCreatedAt(),
                        new RestCreateUserDTO.Company_User(
                                item.getCompany() != null ? item.getCompany().getId() : 0,
                                item.getCompany() != null ? item.getCompany().getName() : null)))
                .collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }


    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        return this.userRepository.findById(id).orElse(null);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUserName(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
