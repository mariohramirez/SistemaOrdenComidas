package com.mario.comidas.service;

import com.mario.comidas.model.USER_ROLE;
import com.mario.comidas.model.User;
import com.mario.comidas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
//Implementa la interfaz UserDetailsService de Spring Security para cargar
//detalles de usuario por nombre de usuario
public class CustomerUserDetailsService implements UserDetailsService {

    //Se inyecta(autowired) el repositorio de usuarios para acceder a los datos de todos los
    //usuarios
    @Autowired
    private UserRepository userRepository;


    @Override
    //Implementacion del metodo loadByUsername de la interfaz, que carga los detalles de un usuario
    //por su nombre de usuario, en este caso el correo electronico
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Se busca el usuario en el repositorio de usuairos utilizando el correo electronico
        User user = userRepository.findByEmail(username);
        //Se no se encuentra el usuario se lanza una excepcion
        if(user==null){
            throw new UsernameNotFoundException("User not found with email"+username);
        }

        //Se obtiene el rol del usuario
        USER_ROLE role = user.getRole();
        //Si el usuario no tiene un rol, se le asigna por defecto el rol de cliente
        if(role==null)role=USER_ROLE.ROLE_CUSTOMER;
        //Se crea una lista de autoridades para el usuario
        List<GrantedAuthority> authorities = new ArrayList<>();

        //Se agrega el rol del usuario a la lista de autoridades
        authorities.add(new SimpleGrantedAuthority(role.toString()));

        //Se devuelve un objeto de UserDetails que representa al usuario, utilizando el correo y la contrasena
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}
