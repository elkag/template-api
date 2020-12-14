package com.template.user.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@RoleMatch(role = "role", message = "Invalid user role")
public class UserModel {

  private Long id;

  @ToString.Exclude
  private String password;

  private String firstName;

  private String lastName;

  @ToString.Exclude
  private String username;

  private List<String> roles;

  public Long getId() {
    return id;
  }

  public UserModel setId(Long id) {
    this.id = id;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public UserModel setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getFirstName() {
    return firstName;
  }

  public UserModel setFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public String getLastName() {
    return lastName;
  }

  public UserModel setLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public UserModel setUsername(String username) {
    this.username = username;
    return this;
  }

  public List<String> getRoles() {
    return roles;
  }

  public UserModel setRoles(List<String> roles) {
    this.roles = roles;
    return this;
  }
}