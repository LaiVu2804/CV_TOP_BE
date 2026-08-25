package vn.ngotien.jobhunter.domain.response.skill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ngotien.jobhunter.domain.response.job.TotalResponse;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobSkillDTO {
    private long id;
    private String name;
    private Instant createAt;
    private Instant updateAt;

    private TotalResponse.JobSkillsDTO skill;
}

