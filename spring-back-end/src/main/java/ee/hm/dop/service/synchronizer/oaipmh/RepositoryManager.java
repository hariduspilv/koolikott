package ee.hm.dop.service.synchronizer.oaipmh;

import static java.lang.String.format;

import ee.hm.dop.model.Repository;
import ee.hm.dop.service.synchronizer.oaipmh.estcore.MaterialParserEstCore;
import ee.hm.dop.service.synchronizer.oaipmh.waramu.MaterialParserWaramu;

public class RepositoryManager {

    private static final String WARAMU_PARSER = "waramu";
    private static final String ESTCORE_PARSER = "estCore";

    public MaterialIterator getMaterialsFrom(Repository repository) throws Exception {
        MaterialIterator materialIterator = getMaterialIterator();

        materialIterator.setParser(getParser(repository));
        materialIterator.connect(repository);

        return materialIterator;
    }

    private MaterialParser getParser(Repository repository) {
        switch (repository.getSchema()) {
            case WARAMU_PARSER:
                return getWaramuMaterialParser();
            case ESTCORE_PARSER:
                return getEstCoreMaterialParser();
            default:
                throw new RuntimeException(format("No parser for schema %s or wrong repository URL",
                        repository.getSchema()));
        }
    }

    protected MaterialIterator getMaterialIterator() {
        return getInstance(MaterialIterator.class);
    }

    protected MaterialParser getWaramuMaterialParser() {
        return getInstance(MaterialParserWaramu.class);
    }

    protected MaterialParser getEstCoreMaterialParser() {
        return getInstance(MaterialParserEstCore.class);
    }

    private <T> T getInstance(Class<T> type) {
        return null;
//        return GuiceInjector.getInjector().getInstance(type);
    }
}
