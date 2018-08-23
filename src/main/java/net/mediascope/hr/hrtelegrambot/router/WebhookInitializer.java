package net.mediascope.hr.hrtelegrambot.router;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SetWebhook;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * @author Евгений Уткин (evgeny.utkin@mediascope.net)
 */
@Component
@Slf4j
public class WebhookInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final TelegramBot bot;

    private final URI root;

    private RetryOperations retry;

    public WebhookInitializer(TelegramBot bot, @Value("server.host")URI root) {
        this.bot = bot;
        this.root = root;
        this.retry = new RetryTemplate();
        ((RetryTemplate) this.retry).setRetryPolicy(new SimpleRetryPolicy(3));
        ((RetryTemplate) this.retry).setBackOffPolicy(new FixedBackOffPolicy());
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String uri = UriComponentsBuilder.fromUri(root).pathSegment("api").pathSegment("rest").pathSegment("update").toUriString();
        String response = retry.execute(context -> {
            BaseResponse baseResponse = bot.execute(new SetWebhook().url(uri));
            if (!baseResponse.isOk()) {
                throw new IncorrectResponseException(baseResponse.description());
            }
            return baseResponse.description();
        });
        log.debug(response);

    }

    public void setRetry(RetryOperations retry) {
        this.retry = retry;
    }

    private static class IncorrectResponseException extends RuntimeException {

        private IncorrectResponseException(String description) {
            super(description);
        }
    }
}
