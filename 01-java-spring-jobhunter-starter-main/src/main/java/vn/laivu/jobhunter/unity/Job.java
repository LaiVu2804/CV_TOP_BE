package vn.laivu.jobhunter.unity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.laivu.jobhunter.util.SecurityUtil;
import vn.laivu.jobhunter.util.constant.Level;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "job_skill",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Resume> resumes;

    @NotBlank(message = "name không được để trống")
    private String name;

    private String location;
    private Double salary;
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private Level level;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String experience;

    private Date startDate;
    private Date endDate;
    private Boolean isActive;
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
}
