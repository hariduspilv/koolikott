package ee.hm.dop.service.synchronizer.oaipmh;

import ee.hm.dop.model.Repository;
import ee.hm.dop.service.synchronizer.oaipmh.estcore.MaterialParserEstCore;
import ee.hm.dop.service.synchronizer.oaipmh.waramu.MaterialParserWaramu;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@AllArgsConstructor
public class RepositoryManager {

    private MaterialIterator materialIterator;
    private MaterialParserWaramu materialParserWaramu;
    private MaterialParserEstCore materialParserEstCore;
    private static final String WARAMU_PARSER = "waramu";
    private static final String ESTCORE_PARSER = "estCore";

    public MaterialIterator getMaterialsFrom(Repository repository) throws Exception {
        MaterialIterator materialIterator = this.materialIterator;

        materialIterator.setParser(getParser(repository));
        materialIterator.connect(repository);

        return materialIterator;
    }

    private MaterialParser getParser(Repository repository) {
        switch (repository.getSchema()) {
            case WARAMU_PARSER:
                return materialParserWaramu;
            case ESTCORE_PARSER:
                return materialParserEstCore;
            default:
                throw new RuntimeException(format("No parser for schema %s or wrong repository URL",
                        repository.getSchema()));
        }
    }
}
