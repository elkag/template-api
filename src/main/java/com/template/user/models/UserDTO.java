package com.template.user.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@RoleMatch(role = "role", message = "Invalid user role")
public class UserDTO {

  private Long id;

  @ToString.Exclude
  private String password;

  private String firstName;

  private String lastName;

  private LocalDateTime registrationDate;

  private Boolean banned;

  @ToString.Exclude
  private String username;

  private List<String> roles;

  public Long getId() {
    return id;
  }

  public UserDTO setId(Long id) {
    this.id = id;
    return this;
  }

  public LocalDateTime getRegistrationDate() {
    return registrationDate;
  }

  public void setRegistrationDate(LocalDateTime registrationDate) {
    this.registrationDate = registrationDate;
  }

  public String getPassword() {
    return password;
  }

  public UserDTO setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getFirstName() {
    return firstName;
  }

  public UserDTO setFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public String getLastName() {
    return lastName;
  }

  public UserDTO setLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public UserDTO setUsername(String username) {
    this.username = username;
    return this;
  }

  public List<String> getRoles() {
    return roles;
  }

  public UserDTO setRoles(List<String> roles) {
    this.roles = roles;
    return this;
  }

  public Boolean getBanned() {
    return banned;
  }

  public UserDTO setBanned(Boolean banned) {
    this.banned = banned;
    return this;
  }
}