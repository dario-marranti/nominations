package com.example.abmsimulado;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "abm.simulado.max-delay-seconds=0"   // sin demora: el primer GET ya es 201
})
@AutoConfigureMockMvc
class NominationControllerTest {

    @Autowired MockMvc mvc;

    private static final String BODY = """
            {"customer_id":"123456","account_id":"987654","card_id":"TOKEN","alias":"CUENTA_PRINCIPAL",
             "request_id":"%s"}""";

    @Test
    void postDevuelveUuidYGetDevuelve201ConId() throws Exception {
        String json = mvc.perform(post("/v1/nominations").contentType(MediaType.APPLICATION_JSON)
                        .content(BODY.formatted("req-1")))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.uuid").exists())
                .andReturn().getResponse().getContentAsString();
        String uuid = JsonPath.read(json, "$.uuid");

        mvc.perform(get("/v1/nominations/" + uuid))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void postIdempotentePorRequestId() throws Exception {
        String a = mvc.perform(post("/v1/nominations").contentType(MediaType.APPLICATION_JSON).content(BODY.formatted("req-2")))
                .andReturn().getResponse().getContentAsString();
        String b = mvc.perform(post("/v1/nominations").contentType(MediaType.APPLICATION_JSON).content(BODY.formatted("req-2")))
                .andReturn().getResponse().getContentAsString();
        String uuidA = JsonPath.read(a, "$.uuid");
        String uuidB = JsonPath.read(b, "$.uuid");
        org.junit.jupiter.api.Assertions.assertEquals(uuidA, uuidB);
    }

    @Test
    void validacionYNotFound() throws Exception {
        mvc.perform(post("/v1/nominations").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/v1/nominations/no-existe"))
                .andExpect(status().isNotFound());
    }
}
