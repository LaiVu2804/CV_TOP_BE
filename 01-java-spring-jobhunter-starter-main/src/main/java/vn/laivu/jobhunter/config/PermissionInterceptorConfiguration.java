package vn.laivu.jobhunter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

    @Value("${api.version}")
    private String apiVersion;

    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Không check interceptor, không check quyền với các endpoint này
        String[] whiteList = {
                "/",
                "/storage/**",
                "/api/" + apiVersion + "/auth/**",
                "/api/" + apiVersion + "/companies/**",
                "/api/" + apiVersion + "/jobs/**",
                "/api/" + apiVersion + "/skills/**",
                "/api/" + apiVersion + "/subscribers/**",
                "/api/" + apiVersion + "/resumes/**",
                "/api/" + apiVersion + "/files"
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}
