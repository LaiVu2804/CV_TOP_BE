package vn.ngotien.jobhunter.unity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ngotien.jobhunter.util.SecurityUtil;

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

    private Instant createAt;
    private Instant updatedAt;
    private String createBy;
    private String updateBy;

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

        this.updatedAt = Instant.now();
    }
}
