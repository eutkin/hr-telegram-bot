package net.mediascope.hr.hrtelegrambot;

import com.pengrad.telegrambot.TelegramBot;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

@SpringBootApplication
public class HrTelegramBotApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(HrTelegramBotApplication.class, args);
    }

    @Bean
    @Profile("work")
    public OkHttpClient workHttpClient() {
        return new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.aeroport.tns", 3128)))
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient httpClient() {
        return new OkHttpClient();
    }

    @Bean
    public TelegramBot telegramBot(@Value("${telegram.token}") String token, OkHttpClient httpClient) {
        return new TelegramBot.Builder(token).okHttpClient(httpClient).build();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new GsonHttpMessageConverter());
    }
}
