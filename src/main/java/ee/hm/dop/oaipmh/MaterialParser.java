package ee.hm.dop.oaipmh;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.service.LanguageService;

/**
 * Created by mart.laus on 14.07.2015.
 */
public class MaterialParser {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(IdentifierIterator.class);
    @Inject
    private LanguageService languageService;

    public Material parseXMLtoMaterial(Document doc) {
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("lom");
        Material material;

        Node nNode = nList.item(0);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNode;
            try {
                material = new Material();

                Node title = eElement.getElementsByTagName("title").item(0);
                List<LanguageString> titles = new ArrayList<>();
                for (int i = 0; i < title.getChildNodes().getLength(); i++) {
                    if (title.getChildNodes().item(i).hasAttributes()) {
                        LanguageString languageString = new LanguageString();
                        languageString.setText(title.getChildNodes().item(i).getTextContent());

                        String languageCode = title.getChildNodes().item(i).getAttributes().item(0).getTextContent();
                        Language language = languageService.getLanguage(languageCode);
                        languageString.setLanguage(language);
                        titles.add(languageString);
                    }
                }
                material.setTitles(titles);

                String materialLanguageString = eElement.getElementsByTagName("language").item(0).getTextContent();
                Language materialLanguage = languageService.getLanguage(materialLanguageString);
                material.setLanguage(materialLanguage);

                Node description = eElement.getElementsByTagName("description").item(0);
                List<LanguageString> descriptions = new ArrayList<>();
                for (int i = 0; i < description.getChildNodes().getLength(); i++) {
                    if (description.getChildNodes().item(i).hasAttributes()) {
                        LanguageString languageString = new LanguageString();
                        languageString.setText(description.getChildNodes().item(i).getTextContent());

                        String languageCode = description.getChildNodes().item(i).getAttributes().item(0)
                                .getTextContent();
                        Language language = languageService.getLanguage(languageCode);
                        languageString.setLanguage(language);
                        descriptions.add(languageString);
                    }
                }
                material.setDescriptions(descriptions);

                List<String> keywords = new ArrayList<>();
                for (int i = 0; i < eElement.getElementsByTagName("keyword").getLength(); i++) {
                    String keyword = eElement.getElementsByTagName("keyword").item(i).getTextContent();
                    keywords.add(keyword);
                }
                material.setSource(eElement.getElementsByTagName("location").item(0).getTextContent());

                DateTime now = new DateTime();
                material.setAdded(now);
                material.setViews((long) 0);
                return material;
            } catch (Exception e) {
                logger.error("Error in parsing material");
                return null;
            }
        }

        return null;
    }
}
