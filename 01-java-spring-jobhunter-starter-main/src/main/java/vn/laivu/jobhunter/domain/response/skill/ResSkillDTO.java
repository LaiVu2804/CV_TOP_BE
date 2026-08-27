package vn.laivu.jobhunter.domain.response.skill;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResSkillDTO {
    private long id;
    private String name;
    private Instant createAt;
    private Instant updateAt;
}
