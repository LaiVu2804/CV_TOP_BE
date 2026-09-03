package vn.laivu.jobhunter.domain.response.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.laivu.jobhunter.unity.constant.Level;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResFetchJobDTO {
    private long id;
    private String name;
    private Integer quantity;
    private Level level;
    private String location;
    private String description;
    private Double salary;
    private String experience;

    private Date startDate;
    private Date endDate;

    private Boolean isActive;

    private List<TotalResponse.JobSkillsDTO> skills;

    private TotalResponse.JobCompanyDTO company;
}
