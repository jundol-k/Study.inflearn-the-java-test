package com.example.inflean_java_test.domain;

import lombok.*;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter @NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String email;

}
