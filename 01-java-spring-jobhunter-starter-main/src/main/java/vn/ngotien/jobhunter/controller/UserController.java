package vn.ngotien.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.ngotien.jobhunter.unity.User;
import vn.ngotien.jobhunter.domain.response.ResUpdateDTO;
import vn.ngotien.jobhunter.domain.response.RestCreateUserDTO;
import vn.ngotien.jobhunter.domain.response.ResultPaginationDTO;
import vn.ngotien.jobhunter.service.UserService;
import vn.ngotien.jobhunter.util.Annotation.ApiMessage;
import vn.ngotien.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<RestCreateUserDTO> createNewUser(@Valid @RequestBody User postManUser)
            throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(postManUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException(
                    "Email: " + postManUser.getEmail() + " đã tồn tại, vui lòng nhập email khác !"
            );
        }
        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        User laiVu = this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.userService.convertToRestCreateDTO(laiVu));
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("user với id = " + id + " không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<RestCreateUserDTO> getUserById(@PathVariable("id") long id)
            throws IdInvalidException {
        User fetcheUser = this.userService.fetchUserById(id);
        if (fetcheUser == null) {
            throw new IdInvalidException("user với id = " + id + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.userService.convertToRestDTO(fetcheUser));
    }

    @PutMapping("/users/{id}")
    @ApiMessage("Update user success !")
    public ResponseEntity<ResUpdateDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        User ericUser = this.userService.hanldleUpdateUser(user);
        if (ericUser == null) {
            throw new IdInvalidException("user với id = " + user.getId() + " không tồn tại ");
        }

        return ResponseEntity.ok(this.userService.convertToRestUpdateDTO(ericUser));
    }

    //fetch all users (lấy tất cả người dùng : Specification ( user not company)
    @GetMapping("/users")

    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            @Filter Specification<User> spec, Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(spec, pageable));
    }

    //Fetch all user (pagination)
//  @GetMapping("/users")
//  public ResponseEntity<ResultPaginationDTO> getAllUsers(
//      @RequestParam("current") Optional<String> currentOptinal,
//      @RequestParam("pageSize") Optional<String> pageSizeOptinal) {
//
//    String sCurrent = currentOptinal.isPresent() ? currentOptinal.get() : "";
//    String sPageSize = pageSizeOptinal.isPresent() ? pageSizeOptinal.get() : "";
//
//    int current = Integer.parseInt(sCurrent);
//    int pageSize = Integer.parseInt(sPageSize);
//
//    Pageable pageable = PageRequest.of(current - 1, pageSize);
//
//    return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(spec, pageable));
//  }
}
