package com.tamboot.security.core;

import com.tamboot.common.tools.collection.CollectionUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TambootUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private static final long serialVersionUID = 3788716689359248665L;

    private Object principal;

    private Object credentials;

    private Collection<GrantedAuthority> authorities;

    private Object details;

    private boolean authenticated = false;

    private String name;

    public TambootUsernamePasswordAuthenticationToken() {
        this(null, null);
        this.authorities = AuthorityUtils.NO_AUTHORITIES;
    }

    public TambootUsernamePasswordAuthenticationToken(UsernamePasswordAuthenticationToken usernamePasswordToken) {
        this(null, null);
        setPrincipal(usernamePasswordToken.getPrincipal());
        setCredentials(usernamePasswordToken.getCredentials());
        setAuthenticated(usernamePasswordToken.isAuthenticated());
        setAuthorities(usernamePasswordToken.getAuthorities());
        if (usernamePasswordToken.getDetails() != null && usernamePasswordToken.getDetails() instanceof WebAuthenticationDetails) {
            WebAuthenticationDetails details = (WebAuthenticationDetails) usernamePasswordToken.getDetails();
            TambootWebAuthenticationDetails tambootDetails = new TambootWebAuthenticationDetails();
            tambootDetails.setRemoteAddress(details.getRemoteAddress());
            tambootDetails.setSessionId(details.getSessionId());
            setDetails(tambootDetails);
        } else {
            setDetails(usernamePasswordToken.getDetails());
        }
    }

    public TambootUsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public void eraseCredentials() {
        eraseSecret(getCredentials());
        eraseSecret(getPrincipal());
        eraseSecret(details);
        credentials = null;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (CollectionUtil.isEmpty(authorities)) {
            return;
        }

        List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>(authorities.size());
        for (GrantedAuthority authority : authorities) {
            authorityList.add(authority);
        }
        this.authorities = authorityList;
    }

    public String getName() {
        if (name != null) {
            return name;
        }

        if (this.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) this.getPrincipal()).getUsername();
        }
        if (this.getPrincipal() instanceof AuthenticatedPrincipal) {
            return ((AuthenticatedPrincipal) this.getPrincipal()).getName();
        }
        if (this.getPrincipal() instanceof Principal) {
            return ((Principal) this.getPrincipal()).getName();
        }

        return (this.getPrincipal() == null) ? "" : this.getPrincipal().toString();
    }

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    private void eraseSecret(Object secret) {
        if (secret instanceof CredentialsContainer) {
            ((CredentialsContainer) secret).eraseCredentials();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TambootUsernamePasswordAuthenticationToken)) {
            return false;
        }

        TambootUsernamePasswordAuthenticationToken test = (TambootUsernamePasswordAuthenticationToken) obj;

        if (!authorities.equals(test.getAuthorities())) {
            return false;
        }

        if ((this.details == null) && (test.getDetails() != null)) {
            return false;
        }

        if ((this.details != null) && (test.getDetails() == null)) {
            return false;
        }

        if ((this.details != null) && (!this.details.equals(test.getDetails()))) {
            return false;
        }

        if ((this.getCredentials() == null) && (test.getCredentials() != null)) {
            return false;
        }

        if ((this.getCredentials() != null)
                && !this.getCredentials().equals(test.getCredentials())) {
            return false;
        }

        if (this.getPrincipal() == null && test.getPrincipal() != null) {
            return false;
        }

        if (this.getPrincipal() != null
                && !this.getPrincipal().equals(test.getPrincipal())) {
            return false;
        }

        return this.isAuthenticated() == test.isAuthenticated();
    }

    @Override
    public int hashCode() {
        int code = 31;

        for (GrantedAuthority authority : authorities) {
            code ^= authority.hashCode();
        }

        if (this.getPrincipal() != null) {
            code ^= this.getPrincipal().hashCode();
        }

        if (this.getCredentials() != null) {
            code ^= this.getCredentials().hashCode();
        }

        if (this.getDetails() != null) {
            code ^= this.getDetails().hashCode();
        }

        if (this.isAuthenticated()) {
            code ^= -37;
        }

        return code;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Principal: ").append(this.getPrincipal()).append("; ");
        sb.append("Credentials: [PROTECTED]; ");
        sb.append("Authenticated: ").append(this.isAuthenticated()).append("; ");
        sb.append("Details: ").append(this.getDetails()).append("; ");

        if (!authorities.isEmpty()) {
            sb.append("Granted Authorities: ");

            int i = 0;
            for (GrantedAuthority authority : authorities) {
                if (i++ > 0) {
                    sb.append(", ");
                }

                sb.append(authority);
            }
        } else {
            sb.append("Not granted any authorities");
        }

        return sb.toString();
    }
}
