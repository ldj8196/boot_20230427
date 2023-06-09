package com.example.boot_20230427.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.boot_20230427.handler.CustomLoginSuccessHandler;
import com.example.boot_20230427.handler.CustomLogoutSuccessHandler;
import com.example.boot_20230427.service.SecurityServiceImpl;
import com.example.boot_20230427.service.SecurityServiceImpl1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration // 환경설정파일, 서버가 구동되기전에 호출됨.
@EnableWebSecurity // 시큐리티를 사용
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    final SecurityServiceImpl memberTableService; // 구현한 서비스 SecurityServiceImpl
    final SecurityServiceImpl1 student2TableService;
    @Bean // 객체를 생성함
    @Order(value = 1) // 순서를 먼저 설정
    public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception {
        log.info("SecurityConfig => {}", "start filter chain");

        // 127.0.0.1:9090/ROOT/student2/login.do 
        // 127.0.0.1:9090/ROOT/student2/loginaction.do
        // 위의 두개의 주소만 필터함.
        http.antMatcher("/student2/login.do")
            .antMatcher("/student2/loginaction.do")
            .authorizeRequests().anyRequest().authenticated().and();

        // 고객용 로그인 처리
        http.formLogin()
            .loginPage("/student2/login.do")                         //  GET 내가 로그인 할 페이지
            .loginProcessingUrl("/student2/loginaction.do")                 // action
            .usernameParameter("id")                                 //  아이디 값
            .passwordParameter("password")                           //  암호 값
            .defaultSuccessUrl("/student2/home.do")                  // 로그인 성공 시 이동할 페이지
            .permitAll();

        // 서비스 등록
        http.userDetailsService(student2TableService); 

        return http.build();
        
    }

    @Bean // 객체를 생성함
    @Order(value = 2) // 순서를 먼저 설정
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("SecurityConfig => {}", "start filter chain");


        // 권한설정
        http.authorizeRequests()
            .antMatchers("/customer/join.do").permitAll()
            .antMatchers("/seller/join.do").permitAll()
            .antMatchers("/admin/join.do").permitAll()

            .antMatchers("/admin","/admin/*").hasAuthority("ROLE_admin") // 주소가 9090/ROOT/admin ~~ 모든것
            .antMatchers("/seller", "/seller/*").hasAnyAuthority("ROLE_admin", "ROLE_seller")
            .antMatchers("/customer", "/customer/*").hasAuthority("ROLE_customer")
            .anyRequest().permitAll();

        // 403페이지 설정(접근권한 불가 시 표시할 화면)
        http.exceptionHandling().accessDeniedPage("/403page.do");

        // 고객용 로그인 처리
        http.formLogin()
            .loginPage("/login.do")                         //  GET 내가 로그인 할 페이지
            .loginProcessingUrl("/loginaction.do")                 // action
            .usernameParameter("id")                                 //  아이디 값
            .passwordParameter("password")                           //  암호 값
            .successHandler(new CustomLoginSuccessHandler())
            //.defaultSuccessUrl("/home.do")                  // 로그인 성공 시 이동할 페이지
            .permitAll();

        // 로그아웃 처리(GET은 안됨 반드시 POST로 호출해야됨)
        http.logout()
            .logoutUrl("/logout.do")
            //.logoutSuccessUrl("/home.do")
            .logoutSuccessHandler(new CustomLogoutSuccessHandler())
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .permitAll();

        // post는 csrf를 전송해야하지만, 주소가 /api 로 시작하는건 csrf가 없어도 허용하도록 설정
        http.csrf().ignoringAntMatchers("/api/**");
        
        // 서비스 등록
        http.userDetailsService(memberTableService); 

        return http.build();
        
    }

    // 정적 자원에 스프링 시큐리티 필터 규칙을 적용하지 않도록 설정, resources/static은 시큐리티 적용받지 않는다.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    // 회원가입에서 사용했던 암호화 알고리즘 설정, 로그인에서도 같은 것을 사용해야 하니까?
    @Bean // 서버 구동시 자동으로 실행됨 @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
