package hello.velogclone.global.config;

import hello.velogclone.global.exception.UnauthorizedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/**", "/css/**", "/js/**", "/", "/images/**").permitAll() // 모든 사용자에게 허용하는 URL 설정
                                .requestMatchers("/api/blogs/{blogId}", "/api/blogs/{blogId}/follower/**", "api/blogs/{blogId}/following/**").permitAll()
                                .requestMatchers("/api/blogs/{blogId}/{postId}").permitAll()
                                .requestMatchers("/api/signup").permitAll()
                                .requestMatchers("/api/blogs/{blogId}/{postId}/comments").permitAll()
                                .requestMatchers("/error404").permitAll()
                                .requestMatchers(("/error/**")).permitAll()
                                .requestMatchers("/home/posts").permitAll()
                                .requestMatchers("/api/blogs/{blogId}/series-list", "/api/blogs/{blogId}/series/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/api/login").permitAll()
                                .loginProcessingUrl("/api/login").permitAll()
                                .defaultSuccessUrl("/", true)
                                .failureUrl("/api/login?error=true").permitAll()
                                .usernameParameter("loginId")
                                .passwordParameter("password")
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/api/logout") // 로그아웃 URL 설정
                                .logoutSuccessUrl("/") //로그아웃시 index.html로 이동하도록
                                .permitAll() // 로그아웃은 모든 사용자에게 허용
                ).exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    String errorMessage = URLEncoder.encode("로그인 후 이용 가능합니다.", StandardCharsets.UTF_8);
                                    response.sendRedirect("/api/login?message=" + errorMessage);
                                }))

                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
