package org.dinky.sso.web;

import lombok.Data;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.JEEContextFactory;
import org.pac4j.core.context.session.JEESessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.core.engine.LogoutLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.http.adapter.JEEHttpActionAdapter;
import org.pac4j.core.util.FindBest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>This controller handles the (application + identity provider) logout process.</p>
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
@Controller
@Data
public class LogoutController {

    private LogoutLogic<Object, JEEContext> logoutLogic;

    @Value("${sso.logout.defaultUrl:#{null}}")
    private String defaultUrl;

    @Value("${sso.logout.logoutUrlPattern:#{null}}")
    private String logoutUrlPattern;

    @Value("${sso.logout.localLogout:#{null}}")
    private Boolean localLogout;

    @Value("${sso.logout.destroySession:#{null}}")
    private Boolean destroySession;

    @Value("${sso.logout.centralLogout:#{null}}")
    private Boolean centralLogout;

    @Autowired
    private Config config;

    @RequestMapping("${sso.logout.path:/logout}")
    public void logout(final HttpServletRequest request, final HttpServletResponse response) {

        final SessionStore<JEEContext> bestSessionStore = FindBest.sessionStore(null, config, JEESessionStore.INSTANCE);
        final HttpActionAdapter<Object, JEEContext> bestAdapter = FindBest.httpActionAdapter(null, config, JEEHttpActionAdapter.INSTANCE);
        final LogoutLogic<Object, JEEContext> bestLogic = FindBest.logoutLogic(logoutLogic, config, DefaultLogoutLogic.INSTANCE);

        final JEEContext context = (JEEContext) FindBest.webContextFactory(null, config, JEEContextFactory.INSTANCE)
                .newContext(request, response, bestSessionStore);
        bestLogic.perform(context, config, bestAdapter, this.defaultUrl, this.logoutUrlPattern,
                this.localLogout, this.destroySession, this.centralLogout);
    }


}
