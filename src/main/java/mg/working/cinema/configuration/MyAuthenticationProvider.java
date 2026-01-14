package mg.working.cinema.configuration;

import mg.working.cinema.model.user.Utilisateur;
import mg.working.cinema.service.user.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;



@Component
public class MyAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UtilisateurService utilisateurService;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String email = auth.getName().trim();
        String password = auth.getCredentials().toString().trim();

        Utilisateur user = utilisateurService.getUtilisateurByMail(email);

        if (user == null) {
            throw new BadCredentialsException("Email ou Mot de passe incorrect");
        }

        // Comparaison simple (pas hashé)
        if (!password.equals(user.getMdp())) {
            throw new BadCredentialsException("Email ou Mot de passe incorrect");
        }

        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getLibelle()))
                .toList();

        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

