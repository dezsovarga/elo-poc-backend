package com.example.elopoc.repository;

import com.example.elopoc.domain.PlayMatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayMatchRepository extends JpaRepository<PlayMatch, Long> {
}
