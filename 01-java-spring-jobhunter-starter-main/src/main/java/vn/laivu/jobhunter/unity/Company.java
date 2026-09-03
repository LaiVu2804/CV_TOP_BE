package vn.laivu.jobhunter.unity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.laivu.jobhunter.util.SecurityUtil;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "companies")
public class Company {

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    List<User> users;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Job> jobs;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "name không được để trống")
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @NotBlank(message = "address không được để trống")
    private String address;

    @NotBlank(message = "logo không được để trống")
    private String logo;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

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
