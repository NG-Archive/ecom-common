# ecom-common

`ecom-common`은 공통 인증/보안/예외 처리 및 테스트 유틸리티를 제공하는 라이브러리형 Gradle 모듈입니다.  
다른 프로젝트에서 공통 기능을 재사용할 때 의존성으로 추가해서 사용합니다.

## 주요 기능

- 인증 관련 공통 구성
- JWT 토큰 처리
- 비밀번호 해시 처리
- 공통 예외 및 에러 처리
- AOP 기반 공통 기능 지원
- 테스트 공통 설정 및 테스트 픽스처
- REST Docs / API 스펙 문서화 보조

## 프로젝트 정보

- Java 21
- Spring Boot 3.5.11
- Reactive WebFlux 기반
- Jakarta EE import 사용
- Gradle 모듈
- Lombok 사용

## 패키지명 안내 

현재는 `site.ng_archive.ecom_common` 패키지를 사용합니다.

## 빌드 산출물

이 프로젝트는 애플리케이션 실행용 `bootJar`가 아니라 **라이브러리 JAR**를 생성합니다.

- `bootJar`: 비활성화
- `jar`: 활성화

## 의존성 추가 방법

### 1) 프로젝트에서 참조

먼저 common 프로젝트를 참조하려는 프로젝트의 상위 폴더에 생성합니다.

폴더 구조 예시:
```sh
ecom % tree -L 1
.
├── ecom-common
├── ecom-member
└── ...
```
 `settings.gradle`에 common 모듈을 포함되도록 `includeBuild '../ecom-common'` 을 추가합니다.

settings.gradle 예시:
```gradle
rootProject.name = 'ecom-member'
includeBuild '../ecom-common'
```

사용하는 모듈의 `build.gradle`에 아래처럼 추가합니다.
```gradle
dependencies {

    ...
    
    implementation 'site.ng-archive:ecom-common'
    testImplementation testFixtures('site.ng-archive:ecom-common')
}
```
