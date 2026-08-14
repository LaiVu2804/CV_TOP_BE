package vn.ngotien.jobhunter.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ngotien.jobhunter.util.constant.Gender;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RestUserDTO {
  private long id;
  private String name;
  private String email;
  private Gender gender;
  private String address;
  private int age;
  private Instant createdAt;
  private Instant updatedAt;
}
