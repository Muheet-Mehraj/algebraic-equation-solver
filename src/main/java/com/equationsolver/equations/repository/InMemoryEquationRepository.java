package com.equationsolver.equations.repository;

import com.equationsolver.equations.core.ExpressionNode;
import com.equationsolver.equations.service.StoredEquation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

/** Thread-safe in-memory store. Data is lost on restart, as the assignment allows. */
@Repository
public class InMemoryEquationRepository implements EquationRepository {

    private final AtomicLong sequence = new AtomicLong();
    private final Map<Long, StoredEquation> store = new ConcurrentSkipListMap<>();

    @Override
    public StoredEquation save(ExpressionNode root) {
        long id = sequence.incrementAndGet();
        StoredEquation equation = new StoredEquation(String.valueOf(id), root);
        store.put(id, equation);
        return equation;
    }

    @Override
    public Optional<StoredEquation> findById(String id) {
        try {
            return Optional.ofNullable(store.get(Long.parseLong(id)));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<StoredEquation> findAll() {
        return List.copyOf(store.values());
    }
}
