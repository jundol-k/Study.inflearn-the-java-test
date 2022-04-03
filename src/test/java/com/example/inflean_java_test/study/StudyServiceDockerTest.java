package com.example.inflean_java_test.study;


import com.example.inflean_java_test.domain.Member;
import com.example.inflean_java_test.domain.Study;
import com.example.inflean_java_test.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Testcontainers
@Slf4j
@ContextConfiguration(initializers = StudyServiceDockerTest.ContainerPropertyInitializer.class)
public class StudyServiceDockerTest {

    @Mock MemberService memberService;

    @Autowired StudyRepository studyRepository;

    @Value("${container.port}") int port;
    @Autowired Environment environment;

    // static 으로 만들지않으면 각 테스트마다 하나씩 컨테이너를 띄운다.
    @Container
    static GenericContainer postgreSQLContainer = new GenericContainer("postgres")
            .withExposedPorts(5432)
            .withEnv("POSTGRES_PASSWORD", "studytest")
            .withEnv("POSTGRES_DB", "studytest");

    @BeforeAll
    static void beforeAll() {
        Slf4jLogConsumer slf4jLogConsumer = new Slf4jLogConsumer(log);
        Integer mappedPort = postgreSQLContainer.getMappedPort(5432);
        System.out.println("mappedPort = " + mappedPort);
        postgreSQLContainer.followOutput(slf4jLogConsumer);
        postgreSQLContainer.start();
    }

    @BeforeEach
    void beforeEach() {
        String containerPort = environment.getProperty("container.port");
        System.out.println("containerPort = " + containerPort);

        String hohoho = environment.getProperty("hohoho");
        System.out.println("hohoho = " + hohoho);

        System.out.println("port = " + port);
    }

    @AfterAll
    static void afterAll() {
        postgreSQLContainer.stop();
    }

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

    // 컨테이너 정보를 스프링 테스트에서 참조하기
    static class ContainerPropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of("container.port=" + postgreSQLContainer.getMappedPort(5432), "hohoho=123")
                    .applyTo(applicationContext.getEnvironment());
        }
    }
}
