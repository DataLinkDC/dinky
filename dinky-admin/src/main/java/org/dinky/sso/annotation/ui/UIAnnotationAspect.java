package org.dinky.sso.annotation.ui;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.dinky.sso.annotation.CommonAspect;

/**
 * The aspect to define the web applications annotations.
 *
 * @author Jerome Leleu
 * @since 3.2.0
 */
@Aspect
public class UIAnnotationAspect extends CommonAspect {

    @Before("@annotation(requireAnyRole)")
    public void beforeRequireAnyRole(final RequireAnyRole requireAnyRole) {
        requireAnyRole(true, requireAnyRole.value());
    }

    @Before("@annotation(requireAllRoles)")
    public void beforeRequireAllRoles(final RequireAllRoles requireAllRoles) {
        requireAllRoles(true, requireAllRoles.value());
    }
}
