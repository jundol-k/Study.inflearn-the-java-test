package com.example.inflean_java_test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.time.Duration;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class) // 메소드명에서 언더스코어를 공백으로 치환한다.
class StudyTest {

    /* Junit5 Assertion */
    @Test
    @DisplayName("예외를 모두 실행하기")
    void assert_all() {
        Study study = new Study(-10);

        assertAll(
                () -> assertNotNull(study),
                () -> assertTrue(study.getLimit() > 0, () -> "스터디 최대 참석 가능 인원은 0 보다 커야 한다."),
                () -> assertEquals(StudyStatus.DRAFT, study.getStatus(), new Supplier<String>() {
                    @Override
                    public String get() {
                        return "스터디를 처음 만들면 상태값이 DRAFT여야 한다.";
                    }
                })
        );
    }

    @Test
    @DisplayName("예외 예측하기")
    void assert_exception() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Study(-10);
        });
        String message = exception.getMessage();
        assertEquals("limit은 0보다 커야한다.", message);
    }


    @Test
    void assert_timeout() {
        // 단점: 람다식 안에있는 구문이 모두 실행될때까지 기다림.
        assertTimeout(Duration.ofMillis(100), () -> {
            new Study(10);
            Thread.sleep(300);
        });
    }

    @Test
    void assert_timeout_preeeptively() {
        // timeout과 같이 기다리지않음.
        // TODO ThreadLocal 부분을 주의해서 사용해야한다. 스레드 관련 우려가 있다면 그냥 timeout을 쓰는게 좋다.
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Study(10);
            Thread.sleep(300);
        });
    }
    /* Junit5 Assertion */

    /* 조건에 따라 테스트 실행하기 */
    @Test
    @DisplayName("시스템 환경변수에따라 테스트하기 -> assumeTrue")
    void assume_true() {
        String test_env = System.getenv("TEST_ENV");
        System.out.println("test_env = " + test_env);
        assumeTrue("LOCAL".equalsIgnoreCase(test_env)); // 맞지 않으므로 아래 테스트를 실행하지 않고 종료함.

        Study study = new Study(10);
        assertEquals(10, study.getLimit());
        System.out.println("study.getLimit() = " + study.getLimit());
    }

    @Test
    @DisplayName("시스템 환경변수에따라 테스트하기 -> assumingThat")
    void assuming_that() {
        String test_env = System.getenv("TEST_ENV");
        System.out.println("test_env = " + test_env);

        assumingThat("LOCAL".equalsIgnoreCase(test_env), () -> {
            // 앞의 조건이 맞을경우에만 실행한다.
            Study study = new Study(100);
            assertEquals(100, study.getLimit());
            System.out.println("study.getLimit() = " + study.getLimit());
        });

        assumingThat("DEV".equalsIgnoreCase(test_env), () -> {
            // 앞의 조건이 맞을경우에만 실행한다.
            Study study = new Study(10);
            assertEquals(10, study.getLimit());
            System.out.println("study.getLimit() = " + study.getLimit());
        });
    }

    @Test
    @DisplayName("어노테이션을 이용한 조건 테스트 -> OS 특정하기")
    @EnabledOnOs({OS.MAC})
    void enable_annotation_os() {
        Study study = new Study(100);
        assertEquals(100, study.getLimit());
        System.out.println("study.getLimit() = " + study.getLimit());
    }

    @Test
    @DisplayName("어노테이션을 이용한 조건 테스트 -> JRE 특정하기")
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_11})
    void enable_annotation_jre() {
        Study study = new Study(100);
        assertEquals(100, study.getLimit());
        System.out.println("study.getLimit() = " + study.getLimit());
    }

    @Test
    @DisplayName("어노테이션을 이용한 조건 테스트 -> 시스템변수 특정하기")
    @EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "local")
    void enable_if_environment_variable() {
        Study study = new Study(100);
        assertEquals(100, study.getLimit());
        System.out.println("study.getLimit() = " + study.getLimit());
    }

    /* 조건에 따라 테스트 실행하기 */

    @BeforeAll
    static void beforeAll() {
        System.out.println("before all");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("after all");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("before each");
    }
}