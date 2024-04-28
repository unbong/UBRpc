package io.unbong.ubrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-04-28 16:06
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ubrpc.app")
public class AppConfigProperties {
    // for app instance
    private String id;

    private String namespace;

    private String env;
}
