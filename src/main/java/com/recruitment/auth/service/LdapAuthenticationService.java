package com.recruitment.auth.service;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapEncoder;
import org.springframework.stereotype.Service;

@Service
public class LdapAuthenticationService {

    private final LdapTemplate ldapTemplate;

    public LdapAuthenticationService(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public boolean authenticate(String email, String password) {

        String safeEmail = LdapEncoder.filterEncode(email);

        return ldapTemplate.authenticate(
                "ou=people",
                "(mail=" + safeEmail + ")",
                password
        );
    }
}