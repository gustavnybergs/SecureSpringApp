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
        // Hämta roller från JWT token (token innehåller "roles": ["USER", "ADMIN"])
        List<String> roles = jwt.getClaimAsStringList("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (roles != null) {
            // Konvertera varje roll till Spring Security format med ROLE_ prefix
            // Spring Security förväntar sig "ROLE_USER" istället för bara "USER"
            authorities.addAll(roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList()));
        }

        // Lägg till eventuella standardbehörigheter från JWT (scope, scp etc.)
        authorities.addAll(delegate.convert(jwt));

        return authorities;
    }
}
