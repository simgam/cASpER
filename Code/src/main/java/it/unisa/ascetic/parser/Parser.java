package it.unisa.ascetic.parser;

import it.unisa.ascetic.storage.beans.PackageBean;
import java.util.List;

public interface Parser {
    List<PackageBean> parse() throws ParsingException;
}
