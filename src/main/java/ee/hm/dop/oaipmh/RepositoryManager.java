package ee.hm.dop.oaipmh;

import static java.lang.String.format;
import ee.hm.dop.guice.GuiceInjector;
import ee.hm.dop.model.Repository;
import ee.hm.dop.oaipmh.waramu.MaterialParserWaramu;

public class RepositoryManager {

    private static final String WARAMU_PARSER = "waramu";

    public MaterialIterator getMaterialsFrom(Repository repository) throws Exception {
        MaterialIterator materialIterator = getMaterialIterator();

        MaterialParser materialParser = getParser(repository);
        materialIterator.setParser(materialParser);
        materialIterator.connect(repository);

        return materialIterator;
    }

    private MaterialParser getParser(Repository repository) {
        MaterialParser parser;

        switch (repository.getSchema()) {
        case WARAMU_PARSER:
            parser = getMaterialParser();
            break;
        default:
            throw new RuntimeException(format("No parser for schema %s or wrong repository URL" , repository.getSchema()));
        }

        return parser;
    }

    protected MaterialIterator getMaterialIterator() {
        return GuiceInjector.getInjector().getInstance(MaterialIterator.class);
    }

    protected MaterialParser getMaterialParser() {
        return GuiceInjector.getInjector().getInstance(MaterialParserWaramu.class);
    }
}
