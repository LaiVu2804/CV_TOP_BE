package vn.ngotien.jobhunter.response;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import vn.ngotien.jobhunter.util.constant.Gender;

@Getter
@Setter
public class ResUpdateDTO {
  private long id;
  private String name;
  private Gender gender;
  private String address;
  private int age;
  private Instant updatedAt;

}
