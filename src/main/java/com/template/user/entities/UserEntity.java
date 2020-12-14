package com.template.user.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  @NotNull(message = "Username cannot be null")
  private String username;

  @Column
  @ToString.Exclude
  private String password;

  @Size(min = 1, max = 30, message
          = "First name must be between 3 and 30 characters")
  @NotNull(message = "Name cannot be null")
  @Column(nullable = false)
  private String firstName;

  @Size(min = 1, max = 30, message
          = "Last name must be between 3 and 30 characters")
  @NotNull(message = "Last name cannot be null")
  @Column(nullable = false)
  private String lastName;

  @Column
  private String image = "";

  @Column(name = "registration_date", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime registrationDate;

  @OneToMany(
          fetch = FetchType.EAGER,
          cascade = CascadeType.ALL,
          orphanRemoval = true)
  @JoinColumn(name="user_id")
  @Enumerated(EnumType.STRING)
  private List<AuthorityEntity> roles;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  public UserEntity setRoles(List<AuthorityEntity> roles) {
    this.roles = roles;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserEntity that = (UserEntity) o;
    return id.equals(that.id) &&
            username.equals(that.username) &&
            password.equals(that.password) &&
            firstName.equals(that.firstName) &&
            lastName.equals(that.lastName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, password, firstName, lastName, image);
  }
}
