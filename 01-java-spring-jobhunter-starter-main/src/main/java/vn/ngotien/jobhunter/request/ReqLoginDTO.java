package vn.ngotien.jobhunter.request;

import jakarta.validation.constraints.NotBlank;

public class ReqLoginDTO {

  @NotBlank(message = "Không được để trống username !")
  private String username;

  @NotBlank(message = "Không được để trống password !")
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
