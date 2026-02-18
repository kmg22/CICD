# Home Server 기반 Kubernetes CI/CD Pipeline 구축
## 1. 프로젝트 개요
본 프로젝트는 Ubuntu 홈서버 환경에서 Kubernetes(K3s) 기반 CI/CD(`Jenkins-Harbor-ArgoCD`) 파이프라인을 직접 설계하고 구축한 DevOps 실습 프로젝트이다. \
온프레미스 환경에서 엔터프라이즈 수준의 DevOps 아키텍처를 재현하는 것을 목표로 하였다.

- 코드 품질 분석 (SonarQube)
- Private Container Registry 구축 (Harbor)
- Testcontainers 기반 CI 통합 테스트
- GitOps 기반 자동 배포 (ArgoCD)
- Blue/Green 무중단 배포
- Prometheus + Grafana 모니터링


---
## 2. 기술 스택

### Application

| Tech | 역할 | 버전 |
|------|------|------|
| **Spring Boot** | 웹 애플리케이션 프레임워크 | 4.0.2 |
| **Java** | 프로그래밍 언어 | 17 LTS |
| **Gradle (Groovy)** | 빌드 도구 | 8.x |
| **MariaDB** | 메인 데이터베이스 | 11.x |

### Infrastructure & Orchestration

| Tech | 역할 | 비고 |
|------|------|------|
| **K3s** | 경량 Kubernetes | Single-node cluster |
| **Docker** | 컨테이너 런타임 | 20.10+ |
| **Helm** | K8s 패키지 관리 | 3.x |
| **Traefik** | Ingress Controller | K3s 기본 포함 |

### CI/CD

| Tech | 역할 | 비고 |
|------|------|------|
| **Jenkins** | CI 빌드 서버 | Docker |
| **SonarQube** | 코드 품질 분석 | Community Edition |
| **Harbor** | Private Container Registry | v2.10.3 |
| **ArgoCD** | GitOps CD | Stable |

### Database

| Tech | 역할 | 비고 |
|------|------|------|
| **MariaDB** | 애플리케이션 DB | Bitnami Helm Chart |
| **PostgreSQL** | SonarQube DB | Docker |

### Monitoring

| Tech | 역할 | 비고 |
|------|------|------|
| **Prometheus** | 메트릭 수집 | kube-prometheus-stack |
| **Grafana** | 메트릭 시각화 | kube-prometheus-stack |

---
## 3. 주요 기능

### 1. GitOps 기반 자동 배포

- Git을 Single Source of Truth로 사용
- ArgoCD가 GitOps 레포 변경을 감지하여 자동 배포
- `git revert`만으로 즉시 롤백 가능

### 2. Blue/Green 무중단 배포

- 두 개의 동일한 환경(Blue/Green)을 항상 유지
- `activeColor` 값 변경만으로 트래픽 즉시 전환
- 문제 발생 시 1분 이내 이전 버전으로 롤백

### 3. 코드 품질 자동 검사

- 모든 빌드마다 SonarQube 정적 분석 수행
- 버그, 취약점, 코드 스멜 자동 검출
- Quality Gate 통과 필수

### 4. 실시간 모니터링

- Prometheus로 JVM, HTTP, DB 메트릭 수집
- Grafana 대시보드로 실시간 시각화
- Spring Boot Actuator 통합

---
