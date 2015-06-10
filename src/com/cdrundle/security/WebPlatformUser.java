package com.cdrundle.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;


public class WebPlatformUser implements UserDetails
{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//~ Instance fields ================================================================================================
    private final String password;
    private final String username;
    private final String orgId;
    private final String realName;
    private final String email;
    private final String orgName;
    private final List<GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final String userId;
    
    //~ Constructors ===================================================================================================
   
    /**
     * @deprecated
     */
    public WebPlatformUser(String userid,String username, String password, boolean enabled,String orgID,String orgName,String realName,String email, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked, GrantedAuthority[] authorities) {
        this(userid,username, password, enabled,orgID,orgName,realName,email, accountNonExpired, credentialsNonExpired, accountNonLocked,
                authorities == null ? null : Arrays.asList(authorities));
    }

    /**
     * Construct the <code>User</code> with the details required by
     * {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider}.
     *
     * @param username the username presented to the
     *        <code>DaoAuthenticationProvider</code>
     * @param password the password that should be presented to the
     *        <code>DaoAuthenticationProvider</code>
     * @param enabled set to <code>true</code> if the user is enabled
     * @param accountNonExpired set to <code>true</code> if the account has not
     *        expired
     * @param credentialsNonExpired set to <code>true</code> if the credentials
     *        have not expired
     * @param accountNonLocked set to <code>true</code> if the account is not
     *        locked
     * @param authorities the authorities that should be granted to the caller
     *        if they presented the correct username and password and the user
     *        is enabled
     *
     * @throws IllegalArgumentException if a <code>null</code> value was passed
     *         either as a parameter or as an element in the
     *         <code>GrantedAuthority[]</code> array
     */
    public WebPlatformUser(String userid,String username, String password, boolean enabled,String orgId,String orgName,String realName,String email, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked, List<GrantedAuthority> authorities) {

        if ((StringUtils.isEmpty(username)) || (password == null)) {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }
        this.userId = userid;
        this.username = username;
        this.password = password;
        this.orgId = orgId;
        this.orgName= orgName;
        this.realName = realName;
        this.email=email;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = Collections.unmodifiableList(sortAuthorities(authorities));
    }

    //~ Methods ========================================================================================================

    public boolean equals(Object rhs) {
        if (!(rhs instanceof WebPlatformUser) || (rhs == null)) {
            return false;
        }

        WebPlatformUser user = (WebPlatformUser) rhs;

        // We rely on constructor to guarantee any User has non-null and >0
        // authorities
        if (!authorities.equals(user.authorities)) {
            return false;
        }

        // We rely on constructor to guarantee non-null username and password
        return (this.getPassword().equals(user.getPassword()) && this.getUsername().equals(user.getUsername())
                && (this.isAccountNonExpired() == user.isAccountNonExpired())
                && (this.isAccountNonLocked() == user.isAccountNonLocked())
                && (this.isCredentialsNonExpired() == user.isCredentialsNonExpired())
                && (this.isEnabled() == user.isEnabled()));
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public int hashCode() {
        int code = 9792;

        if (this.getAuthorities() != null) {
            for (int i = 0; i < this.getAuthorities().size(); i++) {
                code = code * (authorities.get(i).hashCode() % 7);
            }
        }

        if (this.getPassword() != null) {
            code = code * (this.getPassword().hashCode() % 7);
        }

        if (this.getUsername() != null) {
            code = code * (this.getUsername().hashCode() % 7);
        }

        if (this.isAccountNonExpired()) {
            code = code * -2;
        }

        if (this.isAccountNonLocked()) {
            code = code * -3;
        }

        if (this.isCredentialsNonExpired()) {
            code = code * -5;
        }

        if (this.isEnabled()) {
            code = code * -7;
        }

        return code;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }
    

    /**
	 * @return Returns the orgId.
	 */
	public String getOrgId()
	{
		return orgId;
	}

	/**
	 * @return Returns the orgName.
	 */
	public String getOrgName()
	{
		return orgName;
	}

	/**
	 * @return Returns the realName.
	 */
	public String getRealName()
	{
		return realName;
	}

	public String getEmail(){
		return email;
	}

    /**
	 * @return Returns the userID.
	 */
	public String getUserId()
	{
		return userId;
	}

	private static List<GrantedAuthority> sortAuthorities(List<GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority array");
        // Ensure array iteration order is predictable (as per UserDetails.getAuthorities() contract and SEC-xxx)
        SortedSet<GrantedAuthority> sorter = new TreeSet<GrantedAuthority>(new AuthorityComparator());

        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
            sorter.add(grantedAuthority);
        }

        List<GrantedAuthority> sortedAuthorities = new ArrayList<GrantedAuthority>(sorter.size());
        sortedAuthorities.addAll(sorter);

        return sortedAuthorities;
    }
	
	private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {
        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            // Neither should ever be null as each entry is checked before adding it to the set.
            // If the authority is null, it is a custom authority and should precede others.
            if (g2.getAuthority() == null) {
                return -1;
            }

            if (g1.getAuthority() == null) {
                return 1;
            }

            return g1.getAuthority().compareTo(g2.getAuthority());
        }
    }
	
	public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString()).append(": ");
        sb.append("Username: ").append(this.username).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Enabled: ").append(this.enabled).append("; ");
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired).append("; ");
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");

        if (this.getAuthorities() != null) {
            sb.append("Granted Authorities: ");

            for (int i = 0; i < authorities.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }

                sb.append(authorities.get(i));
            }
        } else {
            sb.append("Not granted any authorities");
        }

        return sb.toString();
    }
}
