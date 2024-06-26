package hello.velogclone.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/**", "/css/**", "/js/**", "/").permitAll() // 모든 사용자에게 허용하는 URL 설정
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/api/login")
                                .loginProcessingUrl("/api/login")
                                .defaultSuccessUrl("/", true)
                                .failureUrl("/api/login?error=true")
                                .usernameParameter("loginId")
                                .passwordParameter("password")
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/api/logout") // 로그아웃 URL 설정
                                .logoutSuccessUrl("/") //로그아웃시 index.html로 이동하도록
                                .permitAll() // 로그아웃은 모든 사용자에게 허용
                )
                .csrf().disable();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
