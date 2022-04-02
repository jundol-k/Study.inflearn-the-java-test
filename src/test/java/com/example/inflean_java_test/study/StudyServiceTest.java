package com.example.inflean_java_test.study;

import com.example.inflean_java_test.domain.Member;
import com.example.inflean_java_test.domain.Study;
import com.example.inflean_java_test.domain.StudyStatus;
import com.example.inflean_java_test.member.MemberService;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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

    @Test
    @DisplayName("Mock 객체 확인 & BDD")
    void checkingMock() {
        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);

        Study study = new Study(10, "테스트");
        Member member = new Member();
        member.setId(1L);
        member.setEmail("jundol1@email.com");

        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.save(study)).thenReturn(study);

        given(memberService.findById(1L)).willReturn(Optional.of(member));
        given(studyRepository.save(study)).willReturn(study);

        // When
        studyService.createNewStudy(1L, study);


        // Then
        assertNotNull(study.getOwnerId());
        assertEquals(member.getId(), study.getOwnerId());

        // 호출 수 확인
        verify(memberService, times(1)).notify(study); // 딱 한 번 호출됐어야 한다. 안 하면 에러가 발생함.
        verify(memberService, never()).validate(1L); // 호출되지 말아야 한다.

        then(memberService).should(times(1)).notify(study);
        then(memberService).shouldHaveNoMoreInteractions();

        // 순서가 중요한 경우
        InOrder inOrder = inOrder(memberService);
        inOrder.verify(memberService).notify(study);
        // inOrder.verify(memberService).notify(member);

        verifyNoMoreInteractions(memberService); // 어떠한 액션 이후에 mock을 사용하지 말아야 한다.
    }

    @Test
    @DisplayName("BDD 스타일 API")
    void bdd() {

    }

    @DisplayName("다른 사용자가 볼 수 있도록 스터디를 공개한다 (연습문제)")
    @Test
    void openStudy() {
        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);
        Study study = new Study(10, "더 자바 테스트");

        given(studyRepository.save(study)).willReturn(study);

        // When
        studyService.openStudy(study);

        // Then
        assertEquals(study.getStatus(), StudyStatus.OPENED);
        assertNotNull(study.getOpenedDateTime());
        then(memberService).should(times(1)).notify(study);
    }
}