package com;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Calendar;

@Configuration
public class GlobalConfig {

    /**
     * 跨網域連線設定
     * Reference: https://www.tpisoftware.com/tpu/articleDetails/1415
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                //映射路徑
                registry.addMapping("/**")
                        //允許跨網域請求的來源
                        .allowedOrigins("http://localhost:8080")
                        //允許跨域攜帶cookie資訊，預設跨網域請求是不攜帶cookie資訊的。
                        .allowCredentials(true)
                        //允許使用那些請求方式
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }

    /**
     * Improve rest api request performance.
     * Reference: https://medium.com/@nitinvohra/how-to-improve-performance-of-spring-resttemplate-6af37e0a0f33
     */
    @Bean
    public RestTemplate pooledRestTemplate() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(2000);
        connectionManager.setDefaultMaxPerRoute(2000);

        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .build();

        return new RestTemplateBuilder()
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }

    @Bean
    public Calendar defaultCalendar(){
        return Calendar.getInstance();
    }
}