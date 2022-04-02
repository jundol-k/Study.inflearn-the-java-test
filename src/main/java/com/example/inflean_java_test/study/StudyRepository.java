package com.example.inflean_java_test.study;

import com.example.inflean_java_test.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {
}
