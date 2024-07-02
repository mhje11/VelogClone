package hello.velogclone.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /*
    이미지(정적리소스)를 업로드해도 스프링 부트의 캐시의 기본설정 때문에 바로 반영안돼서
    서버를 껐다켜야 적용됐음 그래서 캐싱기간을 0으로 바로 이미지 로드하게 바꿈
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/profiles/**")
                .addResourceLocations("file:src/main/resources/static/images/profiles/")
                .setCachePeriod(0);
        registry.addResourceHandler("/images/posts/**")
                .addResourceLocations("file:src/main/resources/static/images/posts/")
                .setCachePeriod(0);
    }

}
