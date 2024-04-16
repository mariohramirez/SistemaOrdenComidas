package com.mario.comidas.config;

public class JwtConstant {

    /*Se definen constantes estaticas, secret_key sera utilizada como clave para
    * firmar y verificar los tokens JWT, jwt_header especifica el nombre del encabezado en donde se
    * incluira el token JWT en las solicitudes y respuestas, en este caso son para solicitudes
    * que requieran autorizacion*/
    public static final String  SECRET_KEY="ndsondfjdfnsjdfnsdfjncjkbxchewibcdhxbeyhscbqy8wiehrpthnfdv7dv9hf";

    public static final String JWT_HEADER="Authorization";
}
