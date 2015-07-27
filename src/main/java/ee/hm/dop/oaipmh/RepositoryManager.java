package ee.hm.dop.oaipmh;

import static java.lang.String.format;

import ee.hm.dop.guice.GuiceInjector;
import ee.hm.dop.model.Repository;
import ee.hm.dop.oaipmh.waramu.MaterialParserWaramu;

/**
 * Created by mart.laus on 14.07.2015.
 */
public class RepositoryManager {

    private static final String WARAMU_PARSER = "waramu";

    public MaterialIterator getMaterialsFrom(Repository repository) throws Exception {
        MaterialIterator materialIterator = GuiceInjector.getInjector().getInstance(MaterialIterator.class);

        MaterialParser materialParser = getParser(repository);
        materialIterator.setParser(materialParser);
        materialIterator.connect(repository);

        return materialIterator;
    }

    private MaterialParser getParser(Repository repository) {
        MaterialParser parser;

        switch (repository.getSchema()) {
        case WARAMU_PARSER:
            parser = GuiceInjector.getInjector().getInstance(MaterialParserWaramu.class);
            break;
        default:
            throw new RuntimeException(format("No parser for schema %s", repository.getSchema()));
        }

        return parser;
    }
}
