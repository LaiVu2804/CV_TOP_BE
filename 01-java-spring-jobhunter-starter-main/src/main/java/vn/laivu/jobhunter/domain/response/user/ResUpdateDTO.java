package vn.laivu.jobhunter.domain.response.user;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.laivu.jobhunter.domain.response.job.TotalResponse;
import vn.laivu.jobhunter.unity.constant.Gender;

@Getter
@Setter
public class ResUpdateDTO {
    private long id;
    private String name;
    private Gender gender;
    private String address;
    private int age;
    private Instant updatedAt;
    private String updatedBy;
    private TotalResponse.CompanyUser company;

}
