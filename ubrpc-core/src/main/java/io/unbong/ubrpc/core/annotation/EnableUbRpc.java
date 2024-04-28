package io.unbong.ubrpc.core.annotation;

import io.unbong.ubrpc.core.consumer.ConsumerConfig;
import io.unbong.ubrpc.core.provider.ProviderConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * combinate provider and consumer annotation
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({ProviderConfig.class, ConsumerConfig.class})
public @interface EnableUbRpc {
}
