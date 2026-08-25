package vn.ngotien.jobhunter.domain.response.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ngotien.jobhunter.util.constant.Level;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TotalResponse {

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

    private List<JobSkillsDTO> skills;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JobCompanyDTO {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JobSkillsDTO {
        private long id;
        private String name;
    }
}
