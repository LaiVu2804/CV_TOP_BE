package vn.ngotien.jobhunter.domain.response.company;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RestCompanyDTO {
    private long id;
    private String name;
    private String description;
    private String logo;
    private String address;
    private Instant createdAt;
    private Instant updatedAt;
}
