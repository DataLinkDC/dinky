package org.dinky.sso.component;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.JEESessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.FindBest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The configuration of the pac4j components.
 *
 * @author Jerome Leleu
 * @since 3.2.0
 */
@Configuration
public class ComponentConfig {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    @Autowired(required = false)
    protected Config config;

    @Autowired(required = false)
    protected SessionStore<JEEContext> sessionStore;

    protected SessionStore<JEEContext> getSessionStore() {
        return FindBest.sessionStore(sessionStore, config, JEESessionStore.INSTANCE);
    }

    @Bean
    @RequestScope
    public JEEContext getWebContext() {
        return new JEEContext(request, response, getSessionStore());
    }

    @Bean
    @RequestScope
    public ProfileManager getProfileManager() {
        return new ProfileManager<>(getWebContext());
    }
}
