package vn.laivu.jobhunter.domain.response.resume;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class ResUpdateResumeDTO {
    private Instant updatedAt;
    private String updatedBy;
}