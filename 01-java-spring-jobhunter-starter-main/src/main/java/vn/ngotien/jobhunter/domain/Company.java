package vn.ngotien.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ngotien.jobhunter.util.SecurityUtil;

import java.time.Instant;

@Entity
@Table(name = "copamnies")
@Getter
@Setter
@NoArgsConstructor
public class Companies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String address;

    private String logo;

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
