/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.bnl.channelfinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import gov.bnl.channelfinder.LdapListGroups;
import gov.bnl.channelfinder.Group;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author iusmani
 */
public class CustomLdapUserDetails implements LdapUserDetails{
    
    static Logger log = Logger.getLogger(ChannelManager.class.getName());
    private LdapUserDetails details;
    private Collection<? extends GrantedAuthority> extraAuthorities;
    
    public CustomLdapUserDetails(LdapUserDetails details) {
        this.details = details;
        this.extraAuthorities = getGroups(this.details.getUsername());
    }
    
    @Override
    public String getDn() {
        return details.getDn();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return extraAuthorities;
    }

    @Override
    public String getPassword() {
        return details.getPassword();
    }

    @Override
    public String getUsername() {
        return details.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return details.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return details.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return details.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return details.isEnabled();
    }
    @Override
    public void eraseCredentials() {
        details.eraseCredentials();
    }
    
    private Collection<? extends GrantedAuthority> getGroups(String name) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        LdapListGroups ldapListGroup = new LdapListGroups();
        try {
            List<Group> groups = ldapListGroup.groups(name);
            for (Group group:groups) {
                authorities.add(new SimpleGrantedAuthority(group.getGroup().toUpperCase()));
            }
        } catch (Exception e) {
            log.log(Level.INFO, "Exception: "+ e.getMessage());
        }
            return authorities;
    }
}
