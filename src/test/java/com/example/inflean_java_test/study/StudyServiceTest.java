package com.example.inflean_java_test.study;

import com.example.inflean_java_test.domain.Member;
import com.example.inflean_java_test.domain.Study;
import com.example.inflean_java_test.member.MemberService;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock MemberService memberService;
    @Mock StudyRepository studyRepository;

    @Test
    void createStudyService() {
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("jundol@email.com");
        when(memberService.findById(any())).thenReturn(Optional.of(member)); // Stubbing

        /*doThrow(new IllegalArgumentException()).when(memberService).validate(1L);

        assertThrows(IllegalArgumentException.class, () -> {
            memberService.validate(1L);
        });

        memberService.validate(2L); */
        Study study = new Study(10, "java");
        studyService.createNewStudy(1L, study);
    }

    @Test
    @DisplayName("Stubbing 연습문제")
    void stubbingTest() {
        StudyService studyService = new StudyService(memberService, studyRepository);

        Study study = new Study(10, "테스트");
        Member member = new Member();
        member.setId(1L);
        member.setEmail("jundol1@email.com");

        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.save(study)).thenReturn(study);

        studyService.createNewStudy(1L, study);

        assertNotNull(study.getOwnerId());
        assertEquals(member.getId(), study.getOwnerId());
    }
}