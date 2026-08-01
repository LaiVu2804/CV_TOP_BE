package vn.ngotien.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ngotien.jobhunter.domain.User;
import vn.ngotien.jobhunter.domain.dto.Meta;
import vn.ngotien.jobhunter.domain.dto.ResUpdateDTO;
import vn.ngotien.jobhunter.domain.dto.RestCreateUserDTO;
import vn.ngotien.jobhunter.domain.dto.RestUserDTO;

import vn.ngotien.jobhunter.domain.dto.ResultPaginationDTO;
import vn.ngotien.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User handleCreateUser(User user) {
    return userRepository.save(user);
  }

  public void handleDeleteUser(long id) {
    this.userRepository.deleteById(id);
  }

  public User handleFindUserById(long id) {
    Optional<User> user = this.userRepository.findById(id);
    return user.orElse(null);
  }

  public User hanldleUpdateUser(User reqUser) {
    User currentUser = this.fetchUserById(reqUser.getId());
    if (currentUser == null) {
      currentUser.setName(reqUser.getName());
      currentUser.setGender(reqUser.getGender());
      currentUser.setAge(reqUser.getAge());
      currentUser.setAddress(reqUser.getAddress());

      //Update
      currentUser = this.userRepository.save(currentUser);
    }
    return currentUser;
  }

  public User handleGetUserByUserName(String userName) {
    return this.userRepository.findByEmail(userName);
  }

  public boolean isEmailExist(String email) {
    return this.userRepository.existsByEmail(email);
  }

  public RestCreateUserDTO convertToRestCreateDTO(User user) {
    RestCreateUserDTO res = new RestCreateUserDTO();
    res.setId(user.getId());
    res.setName(user.getName());
    res.setEmail(user.getEmail());
    res.setAge(user.getAge());
    res.setGender(user.getGender());
    res.setCreatedAt(user.getCreateAt());
    res.setAddress(user.getAddress());

    return res;
  }

  public RestUserDTO convertToRestDTO(User user) {
    RestUserDTO res = new RestUserDTO();
    res.setId(user.getId());
    res.setName(user.getName());
    res.setEmail(user.getEmail());
    res.setAge(user.getAge());
    res.setGender(user.getGender());
    res.setCreatedAt(user.getCreateAt());
    res.setAddress(user.getAddress());

    return res;
  }

  public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
    Page<User> pageUser = this.userRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();
    Meta mt = new Meta();

    mt.setPage(pageUser.getNumber() + 1);
    mt.setPageSize(pageUser.getSize());

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
            item.getUpdateAt(),
            item.getCreateAt()))
        .collect(Collectors.toList());

    rs.setResult(listUser);

    return rs;
  }

  //Vừa phân trang vừa tìm kiếm
//  public ResultPaginationDTO fetchAllUser(Specification<User> pageable, Pageable pageable1) {
//    List<User> pageUser = this.userRepository.findAll(pageable);
//    ResultPaginationDTO rs = new ResultPaginationDTO();
//    Meta mt = new Meta();
//
//    rs.setMeta(mt);
//    rs.setResult(pageUser);
//
//    return rs;
//  }


  public ResUpdateDTO convertToRestUpdateDTO(User user) {
    ResUpdateDTO res = new ResUpdateDTO();
    res.setId(user.getId());
    res.setName(user.getName());
    res.setAge(user.getAge());
    res.setAddress(user.getAddress());
    res.setGender(user.getGender());
    res.setUpdatedAt(user.getUpdateAt());

    return res;
  }


  public User fetchUserById(long id) {
    Optional<User> userOptional = this.userRepository.findById(id);
    if (userOptional.isPresent()) {
      return userOptional.get();
    }

    return this.userRepository.findById(id).orElse(null);
  }

  public void updateUserToken(String token, String email){
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
