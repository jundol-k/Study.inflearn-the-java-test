package com.example.inflean_java_test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class) // 메소드명에서 언더스코어를 공백으로 치환한다.
class StudyTest {

    int value = 1;

    /* Junit5 Assertion */
    @Test
    //@DisplayName("예외를 모두 실행하기")
    void assert_all() {
        System.out.println("value++ = " + value++);
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
        System.out.println("value++ = " + value++);
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

    /* tagging , filtering */
    @Test
    @DisplayName("태그 적용하기")
    @Tag("fast")
    void tagging() {
        Study study = new Study(10);
        assertEquals(10, study.getLimit());
    }
    /* tagging , filtering */
    /* custom tag */
    @DisplayName("커스텀 태그 - fast")
    @FastTest
    void customTagFast() {
        Study study = new Study(10);
        assertEquals(10, study.getLimit());
    }

    @DisplayName("커스텀 태그 - slow")
    @SlowTest
    void customTagSlow() {
        Study study = new Study(10);
        assertEquals(10, study.getLimit());
    }
    /* custom tag */

    /* repeat test */
    @DisplayName("테스트 반복하기 - 1")
    @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
    void repeatTest_1(RepetitionInfo repetitionInfo) {
        System.out.println("repetitionInfo.getCurrentRepetition() = " + repetitionInfo.getCurrentRepetition());
        System.out.println("repetitionInfo.getTotalRepetitions() = " + repetitionInfo.getTotalRepetitions());
    }

    @DisplayName("테스트 반복하기 - 2")
    @ParameterizedTest(name = "{index} {displayName} message = {0}")
    @ValueSource(strings = {"오늘도", "재밌는", "하루", "입니다."})
    void repeatParamTest(String message) {
        System.out.println("message = " + message);
    }

    @DisplayName("테스트 반복하기 - 3")
    @ParameterizedTest(name = "{index} {displayName} message = {0}")
    @ValueSource(strings = {"오늘도", "재밌는", "하루", "입니다."})
    @EmptySource
    void repeatParamEmptySourceTest(String message) {
        System.out.println("message = " + message);
    }

    @DisplayName("테스트 반복하기 - 4")
    @ParameterizedTest(name = "{index} {displayName} message = {0}")
    @ValueSource(strings = {"오늘도", "재밌는", "하루", "입니다."})
    @EmptySource
    @NullSource
    void repeatParamNullEmptySourceTest(String message) {
        System.out.println("message = " + message);
    }

    // SAC 변환
    // 암묵적 변환
    @DisplayName("테스트 반복하기 - SAC")
    @ParameterizedTest(name = "{index} {displayName} message = {0}")
    @ValueSource(ints = {10, 20, 40})
    void repeatSacTest(@ConvertWith(StudyConverter.class) Study study) {
        System.out.println("study.getLimit() = " + study.getLimit());
    }

    static class StudyConverter extends SimpleArgumentConverter {

        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "Can only convert to Study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    @Disabled
    @DisplayName("테스트 반복하기 - CsvSource - 1")
    @ParameterizedTest(name = "{index} {displayName} message = {0}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void repeatCsvSourceTest(ArgumentsAccessor argumentsAccessor) {
        Study study = new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        System.out.println("study = " + study);
    }

    @DisplayName("테스트 반복하기 - CsvSource - 2")
    @ParameterizedTest(name = "{index} {displayName} message = {0}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void repeatCsvSourceAggrTest(@AggregateWith(StudyAggregator.class) Study study) {
        System.out.println("study = " + study);
    }

    static class StudyAggregator implements ArgumentsAggregator {

        @Override
        public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
            return new Study(accessor.getInteger(0), accessor.getString(1));
        }
    }

    /* repeat test */

    /* test instance */

    // 테스트 클래스는 테스트 메서드마다 인스턴스가 새로 생성된다.
    // 이유는 테스트간 의존성을 없애기 위함.
    // 테스트 순서는 코드의 위치에 의존해서 매번 순서에 맞게끔 실행되는걸 보장할 수는 없음.
    // JUnit5 부터는 기존 전략을 변경하는 방법이 추가됌. 클래스에 @TestInstance(TestInstance.Lifecycle.PER_CLASS) 선언하면 클래스당 인스턴스가 된다.
    // 선언시 beforeAll, afterAll 이 static 일 필요가 없어짐.

    /* test instance */

    /* test order 지정 */

    // 클래스에 @TestMethodOrder(MethodOrderer.OrderAnnotation.class) 선언
    // 메소드에 @Order(1) 선언.
    // 순서 지정이 가능

    /* test order 지정 */


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