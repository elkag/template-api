package com.template.config.security;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder implements org.springframework.security.crypto.password.PasswordEncoder {

  private static final int WORKLOAD = 12;

  public static String hashPassword(final String plainTextPassword) {
    // TODO hash with argon2
    final String salt = BCrypt.gensalt(WORKLOAD);

    return BCrypt.hashpw(plainTextPassword, salt);
  }

  public static boolean checkPassword(final String plainTextPassword, final String storedHash) {
    if (null == storedHash || !storedHash.startsWith("$2a$")) {
      throw new IllegalArgumentException("Invalid hash provided for comparison");
    }

    return BCrypt.checkpw(plainTextPassword, storedHash);
  }

  @Override
  public String encode(CharSequence rawPassword) {
    return hashPassword(rawPassword.toString());
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return checkPassword(rawPassword.toString(), encodedPassword);
  }
}
