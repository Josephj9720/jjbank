package dev.jordanjoseph.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        //simulate .env for the test
        "ALLOWED_ORIGINS=http://localhost:5173"
})
public class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void optionsPreflight_fromAllowedOrigin_returnsCorsHeader() throws Exception {
        mockMvc
                .perform(options("/api/health")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Vary", org.hamcrest.Matchers.containsString("Origin")))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("GET")));
    }

    @Test
    void optionsPreflight_fromDisallowedOrigin_returns403() throws Exception {
        mockMvc
                .perform(options("/api/health")
                        .header("Origin", "http://notallowed.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void actualGet_fromAllowedOrigin_hasCorsAllowOriginHeader() throws Exception {
        mockMvc
                .perform(get("/api/health")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }
}
