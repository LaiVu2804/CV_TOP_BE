package vn.ngotien.jobhunter.domain.response.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class TotalResponse {

    private Job_Company company;
    private Job_Skill skill;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Job_Skill {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Job_Company {
        private long id;
        private String name;
    }
}
