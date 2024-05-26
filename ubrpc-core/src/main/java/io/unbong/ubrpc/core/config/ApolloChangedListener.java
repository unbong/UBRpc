package io.unbong.ubrpc.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-04-13 18:36
 */
@Slf4j
public class ApolloChangedListener implements ApplicationContextAware {
    ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

//    @ApolloConfigChangeListener({"app3", "application"})
//    private void changeHandler(ConfigChangeEvent changeEvent){
//        for(String key: changeEvent.changedKeys()){
//            ConfigChange change = changeEvent.getChange(key);
//            log.info("found change -> {}", change.toString());
//        }
//
//        // 更新相应的Bean属性值，主要是存在@ConfigurationProperties注解的bean
//        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
//    }
}
