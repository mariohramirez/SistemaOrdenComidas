package com.mario.comidas.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/*Configuracion de seguridad de la aplicacion, la aplicacion hace uso de Spring Security
y JWT(Json Web Tokens) para la autenticacion
*/

//Anota la clase como una clase de configuracion de Spring
@Configuration
//Habilita la configuracion de seguridad web de Spring Security
@EnableWebSecurity
public class AppConfig {


    /*Confifura la cade de filtros de seguridad de la aplicacion, toma un objeto
    * de tipo HttpSecurity en este caso el http y devuelve un objeto SecurityFilterChain*/
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        //Configura  la gestion de sesiones para que la aplicacion no utilice sesiones STATELESS
        http.sessionManagement(managment -> managment.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //Configura la autorizacion para las peticiones HTTP
                .authorizeHttpRequests(Authorize -> Authorize
                        //Las solicitudes que coincidan  con el patron /api/admin/** debe tener uno de los roles especificados
                        .requestMatchers("/api/admin/**").hasAnyRole("RESTAURANT_OWNER", "ADMIN")
                        //Se especifica que las solicitudes que coincidan con el patron /api/** deben estar autenticadas para ser autorizadas
                        .requestMatchers("/api/**").authenticated()
                        //Cualquier otra solicitud que no coincida con los patrones anteriores puede ser accedida sin necesidad de autenticacion o roles especificos
                        .anyRequest().permitAll()
                )
                /*Agega un filtro persolizado JwtTokenValidator antes del filtro BasicAuthenticationFilter*/
                .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
               /*Deshabilita la proteccion CSRF*/
                .csrf(csrf->csrf.disable())
                /*Configura la politica de cors usando un metodo corsConfigurationSoirce personalizado*/
                .cors(cors-> cors.configurationSource(corsConfigurationSource()));
        return http.build();
    }

    //Define y devuelve una configuracion Cors personalizada
    private CorsConfigurationSource corsConfigurationSource(){
        return new CorsConfigurationSource() {
            @Override
            //Recibe un objeto HttpServletRequest
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                //Se crea una nueva instancia de CorsCOnfiguration
                CorsConfiguration cfg = new CorsConfiguration();
                //Se establece las solicitudes CORS permitidas desde el origen http://localhost:3000/
                cfg.setAllowedOrigins(Arrays.asList(
                        "http://localhost:3000/"
                ));
                //Se establece metodos HTTP permitidos, en este caso cualquier metodo
                cfg.setAllowedMethods(Collections.singletonList("*"));
                //Se establecen credenciales de solicitudes CORS, en este caso se permite que el navegador
                //Envie credenciales como cookies o encabezados de autorizacion
                cfg.setAllowCredentials(true);
                //Se establece la lista de encabezados de solicitud permitidos para las solicitudes CORS. En este caso
                //se permite cualquier encabezado
                cfg.setAllowedHeaders(Collections.singletonList("*"));
                //Se establece la lista de encabezados que se pueden exponer a traves de la respuesta CORS en este caso
                //se expone el encabezado Authorization
                cfg.setExposedHeaders(Arrays.asList("Authorization"));
                //Se establece la duracion maxima en segundos que el resultado de la solicitud piede ser almacenado en cache
                cfg.setMaxAge(3600L);
                //Se devuelve la configuracion cfg creada
                return cfg;
            }
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
