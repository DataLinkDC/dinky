package org.dinky.sso.annotation.ws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The "require any role" authorization check.
 *
 * @author Jerome Leleu
 * @since 3.2.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAnyRole {

    String[] value() default {};
}
