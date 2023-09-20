package com.tricon.survey.security;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.tricon.survey.db.entity.DassUser;
import com.tricon.survey.db.entity.DassUserRole;

public final class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(DassUser user) {
    	
        return new JwtUser(
                user.getUuid(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                mapToGrantedAuthorities(user.getRoles()),
                user.getActive(),
                user.getLastPasswordResetDate()

        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(Set<DassUserRole> authorities) {
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getRole()))
                .collect(Collectors.toList());
    }
}
