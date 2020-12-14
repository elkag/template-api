package com.template.logger.entities;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Action, Long> {
}
