package com.mario.comidas.controller;

import com.mario.comidas.config.JwtProvider;
import com.mario.comidas.model.Cart;
import com.mario.comidas.model.USER_ROLE;
import com.mario.comidas.model.User;
import com.mario.comidas.repository.CartRepository;
import com.mario.comidas.repository.UserRepository;
import com.mario.comidas.request.LoginRequest;
import com.mario.comidas.response.AuthResponse;
import com.mario.comidas.service.CustomerUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

//Se marca la clase como controlador Rest
@RestController
//Se define la ruta base para las solicitudes del controlador
@RequestMapping("/auth")
public class AuthController {

    //Se hace inyeccion de dependencias
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    private CartRepository cartRepository;

    //Anotacion para mapear solicitudes HTTP POST
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse>createUserHandler(@RequestBody User user) throws Exception {

        //Se verifica si existe un usuario con el correo proporcionado
        User isEmailExist = userRepository.findByEmail(user.getEmail());
        //Si el correo ya esta en uso se crea una excepcion
        if(isEmailExist!=null){
            throw new Exception("Email is already used with another account");
        }

        //Se crea un nuevo usuario con los datos propocionados
        User createdUser = new User();
        createdUser.setEmail(user.getEmail());
        createdUser.setFullName(user.getFullName());
        createdUser.setRole(user.getRole());
        //Se encripta la contrasena
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));

        //Se guarda el susuario en la base de datos y se crea un carrito asocioado al usuario
        User savedUser = userRepository.save(createdUser);

        Cart cart = new Cart();
        cart.setCustomer(savedUser);
        cartRepository.save(cart);

        //Se crea una instacia de Authentication utilizando el correo y la contrasena
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        //Se establece la autenticacion en el contexto de seguridad de Spring
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Se genera un token JWT usando el proveedor de JWT
        String jwt = jwtProvider.generateToken(authentication);

        //Se crea una respuesta con el token JWT, un mensaje y el rol del usuario
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Register success");
        authResponse.setRole(savedUser.getRole());

        //Se devuelve una respuesta HTTP con la respuesta creada y el codigo de estado 201
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);

    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse>signIn(@RequestBody LoginRequest req){

        String username=req.getEmail();
        String password= req.getPassword();

        Authentication authentication=authenticate(username, password);

        Collection<? extends GrantedAuthority>authorities=authentication.getAuthorities();
        String role=authorities.isEmpty()?null:authorities.iterator().next().getAuthority();

        //Se genera un token JWT usando el proveedor de JWT
        String jwt = jwtProvider.generateToken(authentication);

        //Se crea una respuesta con el token JWT, un mensaje y el rol del usuario
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Login success");
        authResponse.setRole(USER_ROLE.valueOf(role));

        //Se devuelve una respuesta HTTP con la respuesta creada y el codigo de estado 201
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {

        //Se crea una instancia UserDetails con el correo del usuario
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);

        //Si no se encuentra el usuario se lanza una excepcion
        if(userDetails==null){
            throw new BadCredentialsException("Invalid username...");
        }

        //Si las contrasenas no coinciden se lanza una excepcion
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid password...");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

}
