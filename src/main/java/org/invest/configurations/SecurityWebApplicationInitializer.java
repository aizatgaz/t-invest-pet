package org.invest.configurations;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

    @Override
    protected String getDispatcherWebApplicationContextSuffix() {
        return "dispatcher";
    }
}
