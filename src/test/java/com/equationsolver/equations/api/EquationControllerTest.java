package com.equationsolver.equations.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EquationControllerTest {

    @Autowired
    private MockMvc mvc;

    private ResultActions postJson(String url, String body) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    /** Stores an equation and returns its id (tests share one in-memory store, so ids are not assumed). */
    private String store(String equation) throws Exception {
        String json = postJson("/api/equations/store", "{\"equation\":\"" + equation + "\"}")
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(json, "$.equationId");
    }

    @Test
    void storeReturnsCreatedWithIdAndLocation() throws Exception {
        postJson("/api/equations/store", "{\"equation\":\"3x + 2y - z\"}")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/equations/")))
                .andExpect(jsonPath("$.message").value("Equation stored successfully"))
                .andExpect(jsonPath("$.equationId").isString())
                .andExpect(jsonPath("$.postfix").value("3 x * 2 y * + z -"));
    }

    @Test
    void listContainsStoredEquationsInInfixForm() throws Exception {
        String id = store("x^2+y^2-4");
        mvc.perform(get("/api/equations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equations[*].equationId").value(hasItem(id)))
                .andExpect(jsonPath("$.equations[*].equation").value(hasItem("x^2 + y^2 - 4")));
    }

    @Test
    void getById() throws Exception {
        String id = store("3x + 2y - z");
        mvc.perform(get("/api/equations/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equation").value("3x + 2y - z"));
    }

    @Test
    void evaluateMatchesTheAssignmentExample() throws Exception {
        String id = store("3x + 2y - z");
        postJson("/api/equations/" + id + "/evaluate", "{\"variables\":{\"x\":2,\"y\":3,\"z\":1}}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equationId").value(id))
                .andExpect(jsonPath("$.equation").value("3x + 2y - z"))
                .andExpect(jsonPath("$.variables.x").value(2))
                .andExpect(jsonPath("$.result").value(11));
    }

    @Test
    void evaluateReturnsFractionalResults() throws Exception {
        String id = store("x / 4");
        postJson("/api/equations/" + id + "/evaluate", "{\"variables\":{\"x\":1}}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(0.25));
    }

    @Test
    void evaluateEquationWithoutVariablesAcceptsEmptyBody() throws Exception {
        String id = store("2 + 3");
        postJson("/api/equations/" + id + "/evaluate", "{}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(5));
    }

    @Test
    void invalidEquationIsRejectedWithHelpfulMessage() throws Exception {
        postJson("/api/equations/store", "{\"equation\":\"3 + * 4\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("operand was expected")))
                .andExpect(jsonPath("$.path").value("/api/equations/store"));
    }

    @Test
    void blankOrMissingEquationIsRejected() throws Exception {
        postJson("/api/equations/store", "{\"equation\":\"   \"}").andExpect(status().isBadRequest());
        postJson("/api/equations/store", "{}").andExpect(status().isBadRequest());
        postJson("/api/equations/store", "not json").andExpect(status().isBadRequest());
    }

    @Test
    void missingVariablesAreListed() throws Exception {
        String id = store("a + b + c");
        postJson("/api/equations/" + id + "/evaluate", "{\"variables\":{\"b\":1}}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(startsWith("Missing values for variables")))
                .andExpect(jsonPath("$.details[0]").value("a"))
                .andExpect(jsonPath("$.details[1]").value("c"));
    }

    @Test
    void nonNumericOrNullVariableValuesAreRejected() throws Exception {
        String id = store("x + 1");
        postJson("/api/equations/" + id + "/evaluate", "{\"variables\":{\"x\":\"abc\"}}")
                .andExpect(status().isBadRequest());
        postJson("/api/equations/" + id + "/evaluate", "{\"variables\":{\"x\":null}}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void divisionByZeroIsUnprocessable() throws Exception {
        String id = store("1 / x");
        postJson("/api/equations/" + id + "/evaluate", "{\"variables\":{\"x\":0}}")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Division by zero"));
    }

    @Test
    void unknownEquationIsNotFound() throws Exception {
        mvc.perform(get("/api/equations/999999")).andExpect(status().isNotFound());
        postJson("/api/equations/999999/evaluate", "{}").andExpect(status().isNotFound());
    }
}
