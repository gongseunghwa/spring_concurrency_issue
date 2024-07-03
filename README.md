# 데이터 정합성 학습

### 1. <a href="https://github.com/gongseunghwa/spring_concurrency_issue">기본 프로젝트</a> 개요
재고 관리 시스템
- 프로젝트 구조 <br>
![img.png](img/img.png)
- 주요 기능 <br>
![img_1.png](img/img_1.png)
<br>
1. 해당 상품 호출
2. quantity 만큼 상품의 재고 감소
3. 변경사항 저장

### 2. 동시에 100개의 요청을 날리는 테스트(실패)
![img_2.png](img/img_2.png)
![img_3.png](img/img_3.png)

### 3-1. synchronized 활용(실패)
![img_4.png](img/img_4.png)
![img_5.png](img/img_5.png)

### 3-2. synchronized 활용(성공)
- 앞의 방법에서 @Transactional 어노테이션을 제거 >> 성공
<br>
![img_6.png](img/img_6.png)
![img_7.png](img/img_7.png)
<br>
- @Transactional 어노테이션을 붙였을 때 실패하는 이유 >> 
Transaction이 적용된 클래스는 CGLIB에 의해서 런타임에 해당 클래스 기반 프록시가 생성된다

```text
Begin Transaction => method => commit Transaction
```
![img_8.png](img/img_8.png)
<br>
- 위 구조로 기존 메소드를 감싸 실행 전,후로 새로운 코드를 호출한다. <br>
Begin Transaction 과 commit Transaction 은 synchronized 메소드가 아니므로 무결성을 보장받을 수 없다.
- 기존 트랜잭션 메소드가 완료되고 commit 되기까지의 사이 시간에 다른 스레드가 진입하게 되면 레이스 컨디션이 발생한다.

- synchronized 는 하나의 프로세스에서 동작하기 때문에 여러개의 서버 혹은 여러개의 프로그램에서 동작하는 경우 레이스 컨디션을 막을 수 없다.

### 4. Pessimistic Lock (비관적 락)을 활용 
- 모든 트랜잭션이 충돌이 발생한다 가정하고 우선 Lock을 거는 방법
- DB의 Lock 기능을 활용(주로 select for update 구문 사용)
- 비관적 락에는 데드락이 발생할 수 없는가? >> NO
순환참조와 같은 방식으로 데드락이 발생할 수 있다.
- 비관적락의 문제점
1. 성능 저하
비관적 락은 모든 트랜잭션에 대해 Lock을 적용하기 때문에 트래픽이 많은 경우에는 성능저하가 꽤 크다(O(N^2) 정도)
2. 여전히 데드락이 발생할 수 있다.

![img_9.png](img/img_9.png)
- Spring Data JPA는 @Lock을활용하여 쉽게 Lock을 걸 수 있다.
<table>
<tr>
<th>MODE</th> <th>TYPE</th> <th>DESCRIPTION</th>
</tr>
<tr>
    <td>비관적 락</td>
    <td>PESSIMISTIC_READ</td>
    <td>다른 트랜잭션에게 읽기만 허용한다. <br> Shared Lock을 활용하여 락을 거는데 해당 기능을 지원하지 않으면 PESSMISITC_WRITE와 동일하게 동작한다.</td>
</tr>
<tr>
    <td>비관적 락</td>
    <td>PESSIMISTIC_WRITE</td>
    <td>DB에서 제공하는 행 배타잠금(Row Exclusive Lock)을 이용해 잠금을 획득한다. 다른 트랜잭션에서 쓰지도 읽지도 못한다.</td>
</tr>
<tr>
    <td>비관적 락</td>
    <td>PESSIMISTIC_FORCE_INCREMENT</td>
    <td>DB에서 제공하는 행 배타잠금을 이용하여 잠금과 동시에 버전을 증가시킨다.</td>
</tr>
</table>

![img_10.png](img/img_10.png)


### 5. Optimistic Lock(낙관적 락)을 활용 
- 버저닝을 활용하여 락을 건다.
- 버전이 다를 경우 에러를 발생시키고 이후의 동작을 개발자가 설정해주어야 한다.
- DB의 락기능을 사용하는 것이 아닌 JPA를 활용하여 락을 건다.
- 트랜잭션 커밋전에는 충돌을 알 수 없다.
- DB에 락을 잡지는 않아 성능이 저하가 별로 없으나, 충돌이 빈번히 일어난다면 오히려 성능이 떨어질 수 있다.
<table>
<tr>
<th>MODE</th> <th>TYPE</th> <th>DESCRIPTION</th>
</tr>
<tr>
    <td>낙관적 락</td>
    <td>OPTIMISTIC</td>
    <td>낙관적 락을 사용한다.(트랜잭션 전후로 버전확인)</td>
</tr>
<tr>
    <td>낙관적 락</td>
    <td>OPTIMISTIC_FORCE_INCREMENT</td>
    <td>낙관적 락을 사용하면서 추가로 버전을 강제 증가시킨다. </td>
</tr>
</table>

![img.png](img/img100.png)
![img_1.png](img/img_101.png)
![img_2.png](img/img_102.png)

### 6. Named Lock 활용
- 이름을 가진 메타데이터 락 (메타데이터 락: 데이터베이스 객체의 이름이나 구조를 변경하는 경우 획득하는 잠금이다.)
- 하나의 세션에서 해당이름으로 락을 가지면 다른 세션에서 해당 락에 접근할 수 없다.
- 트랜잭션이 종료될 때 자동으로 해제되지 않기 때문에 주의가 필요하다. (직접 해제 or 선점 시간 종료로 해제 가능하다.)
- 데이터 소스를 분리해서 사용하는 것이 좋다. (Connection Pool 을 늘리는 것만으로는 다른 서비스에 영향을 끼칠 수 있다.)
- DB에 거는 락은 테이블에 대한 정합성을 보장하는데 사용하지만 Named Lock 은 보통 비즈니스 로직 혹은 API 등으로 인해 발생하는 임계영역에 락을 걸어 해당 로직의 정합성을 보장한다.
- 타임아웃 구현에 유리하다.

1. GET_LOCK 과 RELEASE_LOCK 함수를 활용해 LOCK을 거는 Repo생성 (실 업무에서는 커넥션 풀 분리가 필요하다.)
![img_3.png](img/img_103.png)
2. 락을 건 후 로직 종료 시점에 락을 푸는 서비스
![img_4.png](img/img_104.png)
![img_5.png](img/img_105.png)

3. 실패이유 >> 분산락의 해제시점과 @Transactional 의 트랜잭션 커밋 시점의 불일치
![img_6.png ](img/img_106.png)
4. Facade 패턴 및 트랜잭션 propagation = Propagation.REQUIRES_NEW 설정을 통해 해결
![img_7.png](img/img_107.png)
![img_8.png](img/img_108.png)
![img_9.png](img/img_109.png)![img_10.png](img/img_110.png)

### 7. lettuce를 활용한 분산락
- 기본적으로 Named Lock과 동작이 비슷하다. setnx를 활용하여 락을 건다.
![img_11.png](img/img_11.png)
- 구현이 간단하지만, 레디스에 부하를 줄 수 있다.
![img_12.png](img/img_12.png)
![img_13.png](img/img_13.png)

### 8. redisson을 활용한 분산락
- sub/pub 방식을 활용하여 락을 건다.
- 레튜스방식에 비해 서버에 부하가 적다.
![img_14.png](img/img_14.png)

