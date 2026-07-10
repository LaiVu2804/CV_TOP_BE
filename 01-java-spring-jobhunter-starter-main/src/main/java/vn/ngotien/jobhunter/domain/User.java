package vn.ngotien.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ngotien.jobhunter.util.SecurityUtil;
import vn.ngotien.jobhunter.util.constant.Gender;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor

public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;

  @NotBlank(message = "email không được để trống")
  private String email;

  @NotBlank(message = "password không được để trống")
  private String password;

  private int age;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  private String address;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String refreshToken;

  private Instant createAt;
  private Instant updateAt;
  private String createBy;
  private String updateBy;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")

  @PrePersist
  private void handleBeforeCreate() {
    this.createBy =
        SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil
            .getCurrentUserLogin()
            .get() : "";

    this.createAt = Instant.now();
  }

  @PreUpdate
  private void handleBeforeUpdateAt() {
    this.updateBy = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil
        .getCurrentUserLogin()
        .get() : "";

    this.updateAt = Instant.now();
  }

//    public void setPassword(String hashPassword) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
}
