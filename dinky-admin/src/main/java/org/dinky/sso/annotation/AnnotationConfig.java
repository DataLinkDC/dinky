package org.dinky.sso.annotation;


import org.dinky.sso.annotation.ui.UIAnnotationAspect;
import org.dinky.sso.annotation.ws.WSAnnotationAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * The configuration for aspects.
 *
 * @author Jerome Leleu
 * @since 3.2.0
 */
@Configuration
@EnableAspectJAutoProxy
public class AnnotationConfig {

    @Bean
    public WSAnnotationAspect wsAnnotationAspect() {
        return new WSAnnotationAspect();
    }

    @Bean
    public UIAnnotationAspect uiAnnotationAspect() {
        return new UIAnnotationAspect();
    }
}
