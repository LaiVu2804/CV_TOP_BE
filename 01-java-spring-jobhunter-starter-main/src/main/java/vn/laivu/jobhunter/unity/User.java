package vn.laivu.jobhunter.unity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.laivu.jobhunter.util.SecurityUtil;
import vn.laivu.jobhunter.unity.constant.Gender;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor

public class User {

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Resume> resumes;

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

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")

    @PrePersist
    private void handleBeforeCreate() {
        this.createdBy =
                SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil
                        .getCurrentUserLogin()
                        .get() : "";

        this.createdAt = Instant.now();
    }

    @PreUpdate
    private void handleBeforeUpdateAt() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil
                .getCurrentUserLogin()
                .get() : "";

        this.updatedAt = Instant.now();
    }

}
