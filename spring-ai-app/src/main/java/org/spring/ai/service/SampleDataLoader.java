package org.spring.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SampleDataLoader implements CommandLineRunner {
    private final DocumentService documentService;

    @Override
    public void run(String... args) throws Exception {
        log.info("샘플 데이터 로딩 시작");

        // 1. 휴가 정책
        String vacationPolicy = """
                # 2024년 휴가 정책
                
                ## 연차 휴가
                - 모든 직원은 연간 15일의 유급 연차를 받습니다.
                - 6개월 이상 근무 시 발생합니다.
                - 미사용 연차는 다음 해로 이월되지 않습니다.
                
                ## 병가
                - 연간 10일의 유급 병가가 제공됩니다
                - 3일 이상 병가 시 의사 진단서가 필요합니다.
                - 병가는 사전 승인이 필요하지 않습니다.
                
                ## 경조사 추가
                - 본인 결혼: 5일
                - 자녀 결혼: 2일
                - 부모 사망: 5일
                - 배우자 부모 사망: 3일
                """;

        // 2. 급여 규정
        String salaryPolicy = """
                # 급여 지급 규정
                
                ## 지급일
                - 매월 25일에 급여가 지급됩니다.
                - 25일이 주말/공휴일인 경우 전 영업일에 지급됩니다.
                
                ## 급여 구성
                - 기본급
                - 직책수당
                - 식대 (월 20만원)
                - 교통비 (월 15만원)
                
                ## 상여금
                - 연 2회 지급 (설날, 추석)
                - 각 기본급의 100%
                
                ## 퇴직금
                - 1년 이상 근속 시 발생
                - 평균 임금 x 근속 연수
                """;

        // 3. 근무 시간
        String workingHours = """
                # 근무 시간 규정
                
                ## 정규 근무 시간
                - 평일: 09:00 ~ 18:00 (점심시간 12:00 ~ 13:00)
                - 주 40시간 근무
                
                ## 유연 근무제
                - 코어 타임: 10:00 ~ 16:00 (필수 근무)
                - 출근 시간: 07:00 ~ 10:00 사이 선택 가능
                - 퇴근 시간: 출근 시간 기준 8시간 후
                
                ## 재택 근무
                - 주 2회까지 가능
                - 사전에 팀장 승인 필요
                - 온라인 회의 참석 필수
                
                ## 초과 근무
                - 사전 승인제
                - 평일: 1.5배 수당
                - 주말/공휴일: 2배 수당
                """;

        // 4. 복리 후생
        String benefits = """
                # 복리후생 제도
                
                ## 건강 검진
                - 연 1회 종합 건강 검진 지원
                - 배우자 포함 (50% 지원)
                
                ## 교육 지원
                - 업무 관련 교육: 100% 지원
                - 연간 한도: 300만원
                - 자격증 취득 시 축하금 지급
                
                ## 경조사 지원
                - 결혼: 100만원
                - 출산: 50만원
                - 부모 사망: 30만원
                
                ## 식사 및 간식
                - 구내 식당 무료 제공
                - 음료 및 간식 상시 제공
                - 저녁 근무 시 식대 지원
                
                ## 동호회 지원
                - 동호회당 월 10만원 지원
                - 최소 5명 이상 구성
                """;

        documentService.addTextDocument("vaction_policy.txt", vacationPolicy);
        documentService.addTextDocument("salary_policy.txt", salaryPolicy);
        documentService.addTextDocument("working_hours.txt", workingHours);
        documentService.addTextDocument("benefits.txt", benefits);

        log.info("샘플 데이터 로딩 완료");
    }
}
