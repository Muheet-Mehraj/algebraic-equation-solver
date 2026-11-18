package com.equationsolver.equations.service;

import com.equationsolver.equations.core.EquationEvaluationException;
import com.equationsolver.equations.core.EquationParser;
import com.equationsolver.equations.core.InvalidEquationException;
import com.equationsolver.equations.core.MissingVariableException;
import com.equationsolver.equations.repository.InMemoryEquationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquationServiceTest {

    private EquationService service;

    @BeforeEach
    void setUp() {
        service = new EquationService(new EquationParser(), new InMemoryEquationRepository());
    }

    @Test
    void storesEquationsWithSequentialIds() {
        assertEquals("1", service.store("3x + 2y - z").id());
        assertEquals("2", service.store("x^2 + y^2 - 4").id());
    }

    @Test
    void listsEquationsReconstructedFromTheTree() {
        service.store("3*x+2*y-z");
        service.store("x ^ 2 + y ^ 2 - 4");
        List<StoredEquation> all = service.findAll();
        assertEquals(2, all.size());
        assertEquals("3x + 2y - z", all.get(0).infix());
        assertEquals("x^2 + y^2 - 4", all.get(1).infix());
        assertEquals("3 x * 2 y * + z -", all.get(0).postfix());
    }

    @Test
    void listIsEmptyInitially() {
        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void evaluatesStoredEquation() {
        String id = service.store("3x + 2y - z").id();
        assertEquals(11.0, service.evaluate(id, Map.of("x", 2.0, "y", 3.0, "z", 1.0)), 1e-9);
    }

    @Test
    void invalidEquationIsNotStored() {
        assertThrows(InvalidEquationException.class, () -> service.store("3 + * 4"));
        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void unknownIdIsNotFound() {
        assertThrows(EquationNotFoundException.class, () -> service.get("42"));
        assertThrows(EquationNotFoundException.class, () -> service.get("abc"));
        assertThrows(EquationNotFoundException.class, () -> service.evaluate("42", Map.of()));
    }

    @Test
    void evaluationErrorsPropagate() {
        String id = service.store("1 / x").id();
        assertThrows(MissingVariableException.class, () -> service.evaluate(id, Map.of()));
        assertThrows(EquationEvaluationException.class, () -> service.evaluate(id, Map.of("x", 0.0)));
    }

    @Test
    void idsStayUniqueUnderConcurrentStores() throws Exception {
        Thread[] threads = new Thread[8];
        for (int t = 0; t < threads.length; t++) {
            threads[t] = new Thread(() -> {
                for (int i = 0; i < 50; i++) {
                    service.store("x + " + i);
                }
            });
            threads[t].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        assertEquals(400, service.findAll().stream().map(StoredEquation::id).distinct().count());
    }
}
