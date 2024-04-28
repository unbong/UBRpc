package io.unbong.ubrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-04-28 15:58
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ubrpc.provider")
public class ProviderConfigurationProperties {

    Map<String, String> metas;
}
