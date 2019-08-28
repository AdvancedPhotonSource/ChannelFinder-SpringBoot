/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.bnl.channelfinder;

/**
 *
 * @author iusmani
 */
import java.util.List;
import javax.naming.directory.SearchControls;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQuery;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapUtils;
import gov.bnl.channelfinder.Group;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import org.springframework.ldap.core.AttributesMapper;

public class LdapListGroups {

    public List<Group> groups(String name) throws Exception{

        LdapContextSource ldapContextSource = new LdapContextSource();
        //LDAP URL
        ldapContextSource.setUrl("ldap://localhost:8389/dc=cf,dc=local");
        ldapContextSource.afterPropertiesSet();
        LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);
        ldapTemplate.afterPropertiesSet();
        
        List<Group> groups = ldapTemplate.search("ou=Group","memberUid="+name, new GroupAttributesMapper());

        return groups;
    }

    /**
     * Custom group attributes mapper, maps the attributes to the group POJO
     */
    private class GroupAttributesMapper implements AttributesMapper<Group> {
        public Group mapFromAttributes(Attributes attrs) throws NamingException {
            Group group = new Group();
            group.setGroup((String)attrs.get("cn").get());

            return group;
        }
    }
}