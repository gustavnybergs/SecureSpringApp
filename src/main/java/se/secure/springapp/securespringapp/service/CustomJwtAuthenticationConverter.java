package se.secure.springapp.securespringapp.service;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final JwtGrantedAuthoritiesConverter delegate = new JwtGrantedAuthoritiesConverter();

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("authorities");
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (roles != null) {
            authorities.addAll(roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
        }

        // Lägg till standard authorities (från "scope", "scp", osv.)
        authorities.addAll(delegate.convert(jwt));

        return authorities;
    }
}
