# 스프링 부트와 AWS로 혼자 구현하는 웹 서비스 책을 보고 구현하면서 생긴 이슈들을 정리

# 백엔드(인텔리제이)

사용한 스프링 부트 버전: 2.7.3

스프링 부트와 AWS로 혼자 구현하는 웹 서비스 책을 보고 구현하면서 생긴 이슈들을 정리했습니다.
책과 같이 참조와 아래내용 보시면 도움이 더 될 것 같습니다.

[전반적인 내용](https://velog.io/@kimsy8979/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%B6%80%ED%8A%B8%EC%99%80-AWS%EB%A1%9C-%ED%98%BC%EC%9E%90-%EA%B5%AC%ED%98%84%ED%95%98%EB%8A%94-%EC%9B%B9%EC%84%9C%EB%B9%84%EC%8A%A4-%ED%9B%84%EA%B8%B0-12)


#1. 4장 - 머스태치 화면 한글 깨지는 현상

  스프링 부트 버전 업으로 인하여 한글이 깨지는 현상 발생
  
  application.yml 에 추가
  
  
      server:
        servlet:
          encoding:
            force-response: true
    

#2. 4장 - 자바스크립트 var -> let 변경

  var는 중복선언이 가능하여 어떤 부분에서 값이 변경되고 문제가 발생하는지 파악하기 힘들다. 
  
  let은 중복선언이 불가하다.
  
      
#3. 5장 - 스프링 시큐리티 변경
  
   `public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeHttpRequests( request -> request
                        .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile").permitAll()
                        .antMatchers("/api/v1/**").hasAnyRole(Role.USER.name())
                        .anyRequest().authenticated())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(login -> login.userInfoEndpoint().userService(customOAuth2UserService));`
                
  람다 형식으로 변경

#4. 5장 - - domain/User에 테이블 이름 변경

  user가 예약어이기 때문에 오류
  @Table(name = "users") 추가
  
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------

# AWS

책에서 사용하는 AMAZON Linux AMI가 더 이상 없어 AMAZON Linux 2 AMI로 했습니다.

#5. 6장 - Java 11설치 

[Java 11 ](https://pompitzz.github.io/blog/Java/awsEc2InstallJDK11.html#jdk-%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5)

  sudo curl -L https://corretto.aws/downloads/latest/amazon-corretto-11-x64-linux-jdk.rpm -o jdk11.rpm // aws coreetto 다운로드
  
  sudo yum localinstall jdk11.rpm //  jdk11 설치

  sudo /usr/sbin/alternatives --config java //  jdk version 선택
  
  java --version // java 버전 확인
  
  rm -rf jdk11.rpm // 다운받은 설치키트 제거
  
#6. 6장 - Hostname 변경

[Hostname 변경](https://docs.aws.amazon.com/ko_kr/AWSEC2/latest/UserGuide/set-hostname.html)

  AMAZON Linux 2로 변경되면서 책에 있는 Hostname 변경이 달라졌습니다.
  
  sudo hostnamectl set-hostname 원하는이름.localdomain
  
#7. 8장 - 메모리 늘리기(gradle 테스트 전 하면 좋은 설정)

[메모리 늘리기](https://velog.io/@shawnhansh/AWS-EC2-%EB%A9%94%EB%AA%A8%EB%A6%AC-%EC%8A%A4%EC%99%91)

  EC2에서 build할 시 램(1GB)이 부족하여 자주 멈추는 현상이 발생할 수 있습니다.
  메모리 스왑을 하여 메모리를 추가 할당하여 멈추는 현상을 줄일 수 있습니다.
  
  sudo dd if=/dev/zero of=/swapfile bs=128M count=16 // 2GB 크기의 스왑 파일을 생성
  
  sudo chmod 600 /swapfile // 스왑 파일의 읽기 및 쓰기 권한을 업데이트
  
  sudo mkswap /swapfile // Linux 스왑 영역을 설정
  
  sudo swapon /swapfile // 스왑 공간에 스왑 파일을 추가하여 즉시 사용할 수 있도록 한다.
  
  sudo vim /etc/fstab 에 들어가
  /swapfile swap swap defaults 0 0 <- 추가
  
  free 명령어를 친 후 swap에 숫자가 있는지 확인
  
#8.  8장 - plain.jar 생성 방지

  Spring Boot 2.5.0 이상 부터는 jar 파일이 두개가 생성 된다.
  이중 plain.jar의 생성 방지를 위해 
  build.grdle에
  
  jar {
	enabled = false
  }
  를 추가한 후 배포를 한다.
  
#9. 8장 - RDS 테이블 생성(???)

  FK가 계속 duplicate가 되서 테이블이 생성이 안되는 오류 발생
  
  CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE //변경 전
  
  CONSTRAINT SPRING_SESSION_ATTRIBUTES FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE // 변경 후
  
  변경하닌 테이블 생성이 됨.(더 조사 필요함)
  
#10. 9장 - Travis CI 대신 GitHubAction 사용

[Travis CI 대신 GitHubAction 사용](https://github.com/jojoldu/freelec-springboot2-webservice/issues/806)
  
  GitHubAction 사용에 자세한 내용은 밑에 참조를 참고해주세요.
  
#11. 10장 - 엔진엑스 설치

  AMAZON Linux 2 AMI에서 설치 명령어 변경
  
  sudo yum install nginx // 변경 전
  
  sudo amazon-linux-extras install nginx1 // 변경 후 
  
