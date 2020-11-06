package gov.bnl.channelfinder;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import gov.bnl.channelfinder.CustomLdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.core.GrantedAuthority;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().anyRequest().authenticated();
        http.httpBasic();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Authentication and Authorization is only needed for non search/query operations
        web.ignoring().antMatchers(HttpMethod.GET, "/**");
    }

    /**
     * External LDAP configuration properties
     */
    @Value("${ldap.enabled:false}")
    boolean ldap_enabled;
    @Value("${ldap.urls:ldaps://localhost:389/}")
    String ldap_url;
    @Value("${ldap.user.dn.pattern}")
    String ldap_user_dn_pattern;
    @Value("${ldap.user.search.base}")
    String ldap_user_search_base;
    @Value("${ldap.groups.search.base}")
    String ldap_groups_search_base;
    @Value("${ldap.groups.search.pattern}")
    String ldap_groups_search_pattern;
    @Value("${ldap.bind.user.dn}")
    String ldap_bind_user_dn;
    @Value("${ldap.bind.user.pass}")
    String ldap_bind_user_pass;
    

    /**
     * Embedded LDAP configuration properties
     */
    @Value("${embedded_ldap.enabled:false}")
    boolean embedded_ldap_enabled;
    @Value("${embedded_ldap.urls:ldaps://localhost:389/}")
    String embedded_ldap_url;
    @Value("${embedded_ldap.base.dn}")
    String embedded_ldap_base_dn;
    @Value("${embedded_ldap.user.dn.pattern}")
    String embedded_ldap_user_dn_pattern;
    @Value("${embedded_ldap.groups.search.base}")
    String embedded_ldap_groups_search_base;
    @Value("${embedded_ldap.groups.search.pattern}")
    String embedded_ldap_groups_search_pattern;

    /**
     * Demo authorization based on in memory user credentials
     */
    @Value("${demo_auth.enabled:false}")
    boolean demo_auth_enabled;

    /**
     * File based authentication
     */
    @Value("${file.auth.enabled:true}")
    boolean file_enabled;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        if (ldap_enabled) {
            DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(ldap_url);
            contextSource.setUserDn(ldap_bind_user_dn);
            contextSource.setPassword(ldap_bind_user_pass);

            contextSource.afterPropertiesSet();

            DefaultLdapAuthoritiesPopulator myAuthPopulator = new DefaultLdapAuthoritiesPopulator(contextSource, ldap_groups_search_base);
            myAuthPopulator.setGroupSearchFilter(ldap_groups_search_pattern);
            myAuthPopulator.setSearchSubtree(true);
            myAuthPopulator.setIgnorePartialResultException(true);

            auth.ldapAuthentication()
            .userDetailsContextMapper(userDetailsContextMapper())
            .userSearchFilter(ldap_user_dn_pattern)
            .userSearchBase(ldap_user_search_base)
            .ldapAuthoritiesPopulator(myAuthPopulator)
            .contextSource(contextSource);
////            
//            auth
//            .ldapAuthentication()
//            .userSearchFilter(ldap_user_dn_pattern)
//            .userSearchBase(ldap_user_search_base)
//            .groupSearchBase(ldap_groups_search_base)
//            .groupSearchFilter(ldap_groups_search_pattern)
//            .contextSource()
//                .url(ldap_url)
//                .port(636)
//                .managerDn(ldap_bind_user_dn)
//                .managerPassword(ldap_bind_user_pass)
//        ;
            
            //ActiveDirectoryLdapAuthenticationProvider adLdapAuthenticationProvider = new ActiveDirectoryLdapAuthenticationProvider(domain_url,ldap_url,"DC=anl,DC=gov");
            //adLdapAuthenticationProvider.setConvertSubErrorCodesToExceptions(true);
            //adLdapAuthenticationProvider.setUseAuthenticationRequestCredentials(true);
//            auth.authenticationProvider(activeDirectoryLdapAuthenticationProvider()).eraseCredentials(true);
//            auth.authenticationProvider(myAuthP);
        }
    
        if (embedded_ldap_enabled) {
            DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(embedded_ldap_url);
            contextSource.afterPropertiesSet();

            DefaultLdapAuthoritiesPopulator myAuthPopulator = new DefaultLdapAuthoritiesPopulator(contextSource, embedded_ldap_groups_search_base);
            myAuthPopulator.setGroupSearchFilter(embedded_ldap_groups_search_pattern);
            myAuthPopulator.setSearchSubtree(true);
            myAuthPopulator.setIgnorePartialResultException(true);


            auth.ldapAuthentication()
                    .userDnPatterns(embedded_ldap_user_dn_pattern)
                    .ldapAuthoritiesPopulator(myAuthPopulator)
                    .groupSearchBase("ou=Group")
                    .contextSource(contextSource);

        }

        if (demo_auth_enabled) {
            auth.inMemoryAuthentication()
                    .withUser("admin").password(encoder().encode("adminPass")).roles("ADMIN").and()
                    .withUser("user").password(encoder().encode("userPass")).roles("USER");
        }
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public AuthenticationManager authenticationManager() {
//        return new ProviderManager(Arrays.asList(activeDirectoryLdapAuthenticationProvider()));
//    }
//    
//    @Bean
//    public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
//        ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(domain_url,ldap_url);
//        provider.setConvertSubErrorCodesToExceptions(true);
//        provider.setUseAuthenticationRequestCredentials(true);
//        provider.setSearchFilter("(sAMAccountName={0})");
//        return provider;
//    }
    @Bean
    public UserDetailsContextMapper userDetailsContextMapper() {
        return new LdapUserDetailsMapper() {
            @Override
            public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
                UserDetails details = super.mapUserFromContext(ctx, username, authorities);
                return new CustomLdapUserDetails((LdapUserDetails) details);
            }
        };
    }
}