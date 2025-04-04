package test;

import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;

public class Test {

    public static void mainKey(String[] args) {


        // Genera una clave de 256 bits (32 bytes)
        Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

        // Codifica la clave en base64 para usarla en tu aplicación
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());

        // Imprime la clave en base64
        System.out.println("Clave base64 generada: " + base64Key);


    }

    public static void main(String[] args) {

    }

}
