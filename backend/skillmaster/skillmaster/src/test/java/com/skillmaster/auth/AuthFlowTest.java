package com.skillmaster.auth;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowTest {

    @Autowired
    MockMvc mvc;

    private String registrar(String email) throws Exception {
        String body = mvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","nombre":"Ana","password":"secreta123"}
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.usuario.email").value(email.toLowerCase()))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.access_token");
    }

    @Test
    void registroLoginYAccesoConToken() throws Exception {
        registrar("Ana@Test.com");

        // Email duplicado -> 409
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@test.com\",\"nombre\":\"Otra\",\"password\":\"secreta123\"}"))
                .andExpect(status().isConflict());

        // Contraseña incorrecta -> 401
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@test.com\",\"password\":\"mala\"}"))
                .andExpect(status().isUnauthorized());

        // Login correcto -> token
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@test.com\",\"password\":\"secreta123\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(body, "$.access_token");

        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana"));

        // Sin token o con token falso -> 401
        mvc.perform(get("/api/carreras")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/carreras").header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized());

        // Con token: iniciar carrera usa el usuario del token
        mvc.perform(post("/api/carreras").header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("EN_CURSO"));
        mvc.perform(get("/api/carreras").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void noSePuedeFinalizarCarreraDeOtroUsuario() throws Exception {
        String tokenA = registrar("a@test.com");
        String tokenB = registrar("b@test.com");

        String body = mvc.perform(post("/api/carreras").header("Authorization", "Bearer " + tokenA))
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(body, "$.id");

        mvc.perform(patch("/api/carreras/{id}/finalizar", id)
                        .param("distanciaMetros", "0")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden());
    }

    @Test
    void validaDatosDeRegistro() throws Exception {
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"no-es-email\",\"nombre\":\"X\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest());
    }
}
