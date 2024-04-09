package org.dinky.sso.annotation.ws;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.dinky.sso.annotation.CommonAspect;

/**
 * The aspect to define the web services annotations.
 *
 * @author Jerome Leleu
 * @since 3.2.0
 */
@Aspect
public class WSAnnotationAspect extends CommonAspect {

    @Before("@annotation(requireAnyRole)")
    public void beforeRequireAnyRole(final RequireAnyRole requireAnyRole) {
        requireAnyRole(false, requireAnyRole.value());
    }

    @Before("@annotation(requireAllRoles)")
    public void beforeRequireAllRoles(final RequireAllRoles requireAllRoles) {
        requireAllRoles(false, requireAllRoles.value());
    }
}
