package vn.laivu.jobhunter.domain.response.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.laivu.jobhunter.unity.Company;
import vn.laivu.jobhunter.unity.constant.Level;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    private Optional<Company> company;

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

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyUser {
        private long id;
        private String name;
    }
}
