package vn.ngotien.jobhunter.domain.response.user;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ngotien.jobhunter.util.constant.Gender;

@Getter
@Setter
public class RestCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private Gender gender;
    private String address;
    private int age;
    private Instant createdAt;
    private Company_User company;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Company_User {
        private long id;
        private String name;
    }
}
