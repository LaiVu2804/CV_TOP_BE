package vn.ngotien.jobhunter.domain.response.job;


import lombok.Getter;
import lombok.Setter;
import vn.ngotien.jobhunter.util.constant.Level;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ResCreateJobDTO {
    private Long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private Level level;
    private String description;
    private Date startDate;
    private Date endDate;
    private String Experience;
    private List<TotalResponse.JobSkillsDTO> skills;
    private TotalResponse.JobCompanyDTO company;
    private boolean isActive;
    private Instant createdAt;
}
