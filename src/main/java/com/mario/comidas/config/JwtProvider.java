package com.mario.comidas.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

//Marca la clase como un componente de servicio de Spring
@Service
public class JwtProvider {

    //Se inicializa una clave secreta para firmar y verificar los tokens JWT, utilizando
    //la clave constante definida anteriormente y convirtiendola en un objeto 'SecretKey'
    private SecretKey key= Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    //Metodo que genera un token JWT a partir de la autenticacion proporcionada
    public String generateToken(Authentication auth){
        //Se obtienen las autoridades(roles) del usuario autenticado
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        //Se llama al metodo populateAuthorithies para convertir las autoridades en una cadena
        //de roles separados por comas
        String roles = populateAuthorities(authorities);

        //Se crea el token JWT usando builder, configurando la fecha de expiracion, el correo electronico
        //los roles del usuario
        String jwt = Jwts.builder().setIssuedAt(new Date())
                .setExpiration((new Date(new Date().getTime()+86400000)))
                .claim("email",auth.getName())
                .claim("authorithies",roles)
                //Se firma el token con la clave secreta definida anteriormente
                .signWith(key)
                //Se compacta el token en una cade JWT y se devuelve
                .compact();
        return jwt;
    }

    //Extrae el correo del token JWT
    public String getEmailFromJwtTOken(String jwt){
        //Se elimina el prefijo Bearer del token
        jwt = jwt.substring(7);

        //Se hace parsing del token para obtener el cuerpo Claims y se extrae el correo
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

        String email = String.valueOf(claims.get("email"));

        return email;
    }

    //Metodo que convierte las autoridades en una cade de roles separados por comas
    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auths = new HashSet<>();

        //Se itera sobre las autoridades y se agregan a un conjuto set para evitar ser duplicados
        for(GrantedAuthority authority:authorities){
            auths.add(authority.getAuthority());
        }
        //Se devuelve una cade de roles separados por comas
        return String.join(",",auths);
    }

}
