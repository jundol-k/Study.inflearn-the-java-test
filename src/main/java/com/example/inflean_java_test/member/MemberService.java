package com.example.inflean_java_test.member;

import com.example.inflean_java_test.domain.Member;
import com.example.inflean_java_test.domain.Study;

import java.util.Optional;

public interface MemberService {

    Optional<Member> findById(Long memberId);

    void validate(Long memberId);

    void notify(Study newstudy);

    void notify(Member member);
}
