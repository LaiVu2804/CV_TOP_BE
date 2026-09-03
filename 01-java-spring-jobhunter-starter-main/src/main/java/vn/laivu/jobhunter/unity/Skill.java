package vn.laivu.jobhunter.unity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.laivu.jobhunter.util.SecurityUtil;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "skills")
    @JsonIgnore
    private List<Job> jobs;

    @NotBlank(message = "name không được để trống")
    private String name;

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
