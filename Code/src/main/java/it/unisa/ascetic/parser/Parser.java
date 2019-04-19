package it.unisa.ascetic.parser;

import it.unisa.ascetic.storage.repository.RepositoryException;

public interface Parser {
    void parse(double soglia) throws ParsingException, RepositoryException;
}
