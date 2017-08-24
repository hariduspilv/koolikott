package ee.hm.dop.service.synchronizer.oaipmh;

import static java.lang.String.format;

import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.model.Repository;
import ee.hm.dop.service.synchronizer.oaipmh.estcore.MaterialParserEstCore;
import ee.hm.dop.service.synchronizer.oaipmh.waramu.MaterialParserWaramu;

public class RepositoryManager {

    private static final String WARAMU_PARSER = "waramu";
    private static final String ESTCORE_PARSER = "estCore";

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
                parser = getWaramuMaterialParser();
                break;
            case ESTCORE_PARSER:
                parser = getEstCoreMaterialParser();
                break;
            default:
                throw new RuntimeException(format("No parser for schema %s or wrong repository URL",
                        repository.getSchema()));
        }

        return parser;
    }

    protected MaterialIterator getMaterialIterator() {
        return GuiceInjector.getInjector().getInstance(MaterialIterator.class);
    }

    protected MaterialParser getWaramuMaterialParser() {
        return GuiceInjector.getInjector().getInstance(MaterialParserWaramu.class);
    }

    protected MaterialParser getEstCoreMaterialParser() {
        return GuiceInjector.getInjector().getInstance(MaterialParserEstCore.class);
    }
}
