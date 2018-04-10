package ee.hm;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;

import java.io.IOException;
import java.util.Collection;

class ConditionalCopyProcessor extends UpdateRequestProcessor {

    public ConditionalCopyProcessor(UpdateRequestProcessor next) {
        super(next);
    }

    @Override
    public void processAdd(AddUpdateCommand cmd) throws IOException {

        SolrInputDocument doc = cmd.getSolrInputDocument();
        Collection<Object> tagUpVotes = doc.getFieldValues("tag_up_vote");
        if (tagUpVotes != null) {
            tagUpVotes.forEach(tagUpVote -> {
                String upVote = (String) tagUpVote;
                String tag = upVote.substring(0, upVote.lastIndexOf("_"));
                int count = Integer.parseInt(upVote.substring(upVote.lastIndexOf("_") + 1));

                for (int i = 0; i < count; i++) {
                    doc.addField("tag", tag);
                }
            });
        }

        // pass it up the chain
        super.processAdd(cmd);
    }
}