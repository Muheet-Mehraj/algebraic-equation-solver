package com.equationsolver.equations.repository;

import com.equationsolver.equations.core.ExpressionNode;
import com.equationsolver.equations.service.StoredEquation;

import java.util.List;
import java.util.Optional;

/** Storage abstraction so the in-memory store can be swapped for a database later. */
public interface EquationRepository {

    /** Persists the tree under a newly generated id. */
    StoredEquation save(ExpressionNode root);

    Optional<StoredEquation> findById(String id);

    /** All equations, oldest first. */
    List<StoredEquation> findAll();
}
