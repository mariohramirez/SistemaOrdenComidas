package com.mario.comidas.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

//Extiende OncePerRequestFilter que garantiza que el filtro se ejecute una vez por cada solicitud
public class JwtTokenValidator extends OncePerRequestFilter {

    @Override
    //Implementa el metodo doFilterInternal, donde se realiza la logica de filtrado para validad
    //y procesar el token JWT
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //Obtiene el token JWT del encabezado HTTP utilizando el nombre definido en la cosntante
        String jwt=request.getHeader(JwtConstant.JWT_HEADER);

        //Bearer token

        //Verifica si recibio un token JWT
        if(jwt!=null){
            //Elimina el prefijo bearer
            jwt = jwt.substring(7);
            try{
                //Crea una instancia de secret key a partri de la clave definida en la constante
                SecretKey key= Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
                //Hace parsing del token JWT y obtienen el claim
                Claims claims= Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

                //Obtiene el correo electronico
                String email = String.valueOf(claims.get("email"));
                //Obtiene los roles
                String authorities = String.valueOf(claims.get("authorities"));

                //ROLE_CUSTOMER, ROLE_ADMIN

                //Convierte la cadena de roles en una lista de GrantedAuthorithy
                List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                //Crea un objeto de tipo Authentication con el correo y los roles
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auth);
                //Establece la autenticacion en el contexto de seguridad de Sprin
                SecurityContextHolder.getContext().setAuthentication(authentication);

                
            }
            //Captura cualquier excepcion
            catch (Exception e) {
                throw new BadCredentialsException("Invalid token....");
            }
        }

        //Continua con la cadena de filtros
        filterChain.doFilter(request, response);

    }
}
