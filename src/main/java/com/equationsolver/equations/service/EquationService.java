package com.equationsolver.equations.service;

import com.equationsolver.equations.core.EquationParser;
import com.equationsolver.equations.core.Evaluator;
import com.equationsolver.equations.repository.EquationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EquationService {

    private final EquationParser parser;
    private final EquationRepository repository;

    public EquationService(EquationParser parser, EquationRepository repository) {
        this.parser = parser;
        this.repository = repository;
    }

    /** Parses the text (validating it) and stores the resulting tree. */
    public StoredEquation store(String equation) {
        return repository.save(parser.parse(equation));
    }

    public List<StoredEquation> findAll() {
        return repository.findAll();
    }

    public StoredEquation get(String id) {
        return repository.findById(id).orElseThrow(() -> new EquationNotFoundException(id));
    }

    public double evaluate(String id, Map<String, Double> variables) {
        return Evaluator.evaluate(get(id).root(), variables);
    }
}
