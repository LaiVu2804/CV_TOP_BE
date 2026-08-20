package vn.ngotien.jobhunter.unity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private Instant createAt;
    private Instant updateAt;
    private String createBy;
    private String updateBy;
}
