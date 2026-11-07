package com.example.fcmnode;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "fcm.key-file=no-existe.json")
@AutoConfigureMockMvc
class FcmNodeApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void rejectsBodyWithoutMessage() throws Exception {
        mvc.perform(post("/messages").contentType(MediaType.APPLICATION_JSON).content("{\"topic\":\"general\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("message")));
    }

    @Test
    void explainsWhereTheKeyIsMissing() throws Exception {
        mvc.perform(post("/messages").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":{\"topic\":\"general\"}}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", containsString("no-existe.json")));
    }
}
