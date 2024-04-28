package io.unbong.ubrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-04-28 16:33
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ubrpc.consumer")
public class ConsumerConfigurationProperties {

    // for ha and governance
    private int retries;

    private int timeout;

    private int faultLimit;

    private int halfOpenInitialDelay;

    private int halfOpenDelay;

    private int grayRatio;
}
