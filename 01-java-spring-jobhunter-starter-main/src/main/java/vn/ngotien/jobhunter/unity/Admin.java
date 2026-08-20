package vn.ngotien.jobhunter.unity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor

public class Admin {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotBlank(message = "email không được để trống")
  private String email;

  @NotBlank(message = "password không được để trống")
  private String password;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String refreshToken;

  private Instant createAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")

  @PrePersist
  private void handleBeforeCreate() {
    this.createAt = Instant.now();
  }

}
