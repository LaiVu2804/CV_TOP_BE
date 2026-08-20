package vn.ngotien.jobhunter.unity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    @PrePersist
    private void handleBeforeCreate() {
        this.createAt = Instant.now();
    }

    private Instant updatedAt;
    private String createBy;
    private String updateBy;
}
