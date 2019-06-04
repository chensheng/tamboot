package com.tamboot.security.core;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

public class TambootUserDetails implements UserDetails {
	private static final long serialVersionUID = -4813580920634229504L;

	private static final Collection<? extends GrantedAuthority> EMPTY_AUTHORITIES = new ArrayList<GrantedAuthority>(0);

	private final Long userId;

	private String password;

	private final String username;

	private final Set<GrantedAuthority> authorities;

	private final Set<String> roles;

	private final boolean accountNonExpired;

	private final boolean accountNonLocked;

	private final boolean credentialsNonExpired;

	private final boolean enabled;

	/**
	 * Construct the <code>User</code> with the details
	 *
	 * @param userId
	 * @param username
	 * @param password
	 * @param enabled set to <code>true</code> if the user is enabled
	 * @param accountNonExpired set to <code>true</code> if the account has not expired
	 * @param credentialsNonExpired set to <code>true</code> if the credentials have not expired
	 * @param accountNonLocked set to <code>true</code> if the account is not locked
	 * @param authorities the authorities that should be granted to the caller if they presented the correct username and password and the user is enabled.
	 */
	private TambootUserDetails(Long userId, String username, String password, boolean enabled, boolean accountNonExpired,
                               boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
		if (authorities == null) {
			authorities = EMPTY_AUTHORITIES;
		}
		
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.accountNonExpired = accountNonExpired;
		this.credentialsNonExpired = credentialsNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
		this.roles = initRoles();
	}

	private Set<String> initRoles() {
		Set<String> roles = new HashSet<String>();
		for (GrantedAuthority authority : authorities) {
			String role = authority.getAuthority();
			if (role.startsWith("ROLE_")) {
				roles.add(role.substring(5));
			} else {
			    roles.add(role);
            }
		}
		return Collections.unmodifiableSet(roles);
	}

	public Collection<String> getRoles() {
	    return roles;
    }
	
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}
	
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}
	
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}
	
	public void eraseCredentials() {
		password = null;
	}
	
	private static SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
		SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<GrantedAuthority>(new AuthorityComparator());
		
		for (GrantedAuthority grantedAuthority : authorities) {
			Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
			sortedAuthorities.add(grantedAuthority);
		}
		
		return sortedAuthorities;
	}
	
	private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {
		private static final long serialVersionUID = -5843605002276190955L;

		public int compare(GrantedAuthority g1, GrantedAuthority g2) {
			if (g2.getAuthority() == null) {
				return -1;
			}
			
			if (g1.getAuthority() == null) {
				return 1;
			}
			
			return g1.getAuthority().compareTo(g2.getAuthority());
		}
	}


	@Override
	public boolean equals(Object rhs) {
		if (rhs instanceof TambootUserDetails) {
			return username.equals(((TambootUserDetails) rhs).username);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return username.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(": ");
		sb.append("Username: ").append(this.username).append("; ");
		sb.append("Password: [PROTECTED]; ");
		sb.append("Enabled: ").append(this.enabled).append("; ");
		sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
		sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired).append("; ");
		sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");

		if (!authorities.isEmpty()) {
			sb.append("Granted Authorities: ");

			boolean first = true;
			for (GrantedAuthority auth : authorities) {
				if (!first) {
					sb.append(",");
				}
				first = false;
				sb.append(auth);
			}
		} else {
			sb.append("Not granted any authorities");
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @param userId not null
	 * @param username not null
	 * @param password
	 * @return
	 */
	public static TambootUserDetailsBuilder init(Long userId, String username, String password) {
		Assert.notNull(userId, "userId property must not be null");
		Assert.notNull(username, "username property must not be null");
		return new TambootUserDetailsBuilder().init(userId, username, password);
	}

	public static class TambootUserDetailsBuilder {
		private Long userId;
		private String username;
		private String password;
		private Collection<GrantedAuthority> authorities;
		private Collection<String> functionCodes;
		private Map<String, Collection<String>> functionCodeMap;
		private String menuJson;
		private boolean accountExpired;
		private boolean accountLocked;
		private boolean credentialsExpired;
		private boolean disabled;

		private TambootUserDetailsBuilder() {
		}

		private TambootUserDetailsBuilder init(Long userId, String username, String password) {
			this.userId = userId;
			this.username = username;
			this.password = password;
			return this;
		}

		/**
		 * Populates the roles.
		 *
		 * @param roles the roles for this user (i.e. USER, ADMIN, etc). Cannot contain null values or start with "ROLE_"
		 * @return the {@link TambootUserDetailsBuilder} for method chaining (i.e. to populate
		 * additional attributes for this user)
		 */
		public TambootUserDetailsBuilder roles(String... roles) {
			if (roles == null || roles.length == 0) {
				return this;
			}
			
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(roles.length);
			for (String role : roles) {
				Assert.isTrue(!role.startsWith("ROLE_"), role + " cannot start with ROLE_ (it is automatically added)");
				authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
			}
			this.authorities = new ArrayList<GrantedAuthority>(authorities);
			return this;
		}

		/**
		 * Defines if the account is expired or not. Default is false.
		 *
		 * @param accountExpired true if the account is expired, false otherwise
		 * @return the {@link TambootUserDetailsBuilder} for method chaining (i.e. to populate
		 * additional attributes for this user)
		 */
		public TambootUserDetailsBuilder accountExpired(boolean accountExpired) {
			this.accountExpired = accountExpired;
			return this;
		}

		/**
		 * Defines if the account is locked or not. Default is false.
		 *
		 * @param accountLocked true if the account is locked, false otherwise
		 * @return the {@link TambootUserDetailsBuilder} for method chaining (i.e. to populate
		 * additional attributes for this user)
		 */
		public TambootUserDetailsBuilder accountLocked(boolean accountLocked) {
			this.accountLocked = accountLocked;
			return this;
		}

		/**
		 * Defines if the credentials are expired or not. Default is false.
		 *
		 * @param credentialsExpired true if the credentials are expired, false otherwise
		 * @return the {@link TambootUserDetailsBuilder} for method chaining (i.e. to populate
		 * additional attributes for this user)
		 */
		public TambootUserDetailsBuilder credentialsExpired(boolean credentialsExpired) {
			this.credentialsExpired = credentialsExpired;
			return this;
		}

		/**
		 * Defines if the account is disabled or not. Default is false.
		 *
		 * @param disabled true if the account is disabled, false otherwise
		 * @return the {@link TambootUserDetailsBuilder} for method chaining (i.e. to populate
		 * additional attributes for this user)
		 */
		public TambootUserDetailsBuilder disabled(boolean disabled) {
			this.disabled = disabled;
			return this;
		}

		/**
		 * Build {@link TambootUserDetails}
		 * @return
		 */
		public TambootUserDetails build() {
			return new TambootUserDetails(userId, username, password, !disabled, !accountExpired,
					!credentialsExpired, !accountLocked, authorities);
		}
	}
	

}
