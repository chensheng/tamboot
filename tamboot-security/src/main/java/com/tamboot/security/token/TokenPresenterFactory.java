package com.tamboot.security.token;

import com.tamboot.common.utils.StringUtils;
import com.tamboot.security.config.TambootSecurityProperties;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class TokenPresenterFactory {
    public static final String HEADER_NAME_TOKEN_PRESENTER = "Token-Presenter";

    private TokenPresenter defaultTokenPresenter;

    private TambootSecurityProperties properties;

    private ApplicationContext applicationContext;

    private Map<String, TokenPresenter> internalPresenters = new HashMap<String, TokenPresenter>();

    public TokenPresenterFactory(TambootSecurityProperties properties, ApplicationContext applicationContext) {
        this.properties = properties;
        this.applicationContext = applicationContext;
        this.initInternalPresenters();
    }

    public TokenPresenter get(HttpServletRequest request) {
        String presenterName = request.getHeader(HEADER_NAME_TOKEN_PRESENTER);
        if (StringUtils.isEmpty(presenterName)) {
            return defaultTokenPresenter;
        }

        TokenPresenter internalPresenter = this.doGetInternal(presenterName);
        if (internalPresenter != null) {
            return internalPresenter;
        }

        TokenPresenter externalPresenter = this.doGetExternal(presenterName);
        if (externalPresenter != null) {
            return externalPresenter;
        }

        return defaultTokenPresenter;
    }

    private void initInternalPresenters() {
        defaultTokenPresenter = new CookieTokenPresenter(properties);
        register(defaultTokenPresenter);
        register(new CookieTokenPresenter(properties));
        register(new HeaderTokenPresenter(properties));
    }

    private TokenPresenter doGetInternal(String presenterName) {
        return internalPresenters.get(presenterName);
    }

    private TokenPresenter doGetExternal(String presenterName) {
        Map<String, TokenPresenter> presenterMap = null;
        try {
            presenterMap = applicationContext.getBeansOfType(TokenPresenter.class);
        } catch (BeansException e) {
        }
        if (CollectionUtils.isEmpty(presenterMap)) {
            return null;
        }

        for (TokenPresenter presenter : presenterMap.values()) {
            if (presenterName.equals(presenter.getName())) {
                return presenter;
            }
        }

        return null;
    }

    private void register(TokenPresenter presenter) {
        if (presenter != null) {
            internalPresenters.put(presenter.getName(), presenter);
        }
    }
}
