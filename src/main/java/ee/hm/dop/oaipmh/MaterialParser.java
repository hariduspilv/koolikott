package ee.hm.dop.oaipmh;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ee.hm.dop.model.Author;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.service.AuthorService;
import ee.hm.dop.service.LanguageService;
import ezvcard.Ezvcard;
import ezvcard.VCard;

public abstract class MaterialParser {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    public Material parse(Document doc) throws ParseException {
        Material material;

        try {
            doc.getDocumentElement().normalize();

            material = new Material();

            setIdentifier(material, doc);

            setTitles(material, doc);
            setLanguage(material, doc);
            setDescriptions(material, doc);
            setSource(material, doc);
            setTags(material, doc);
            setLearningResourceType(material, doc);
            setTaxon(material, doc);
            setAuthors(material, doc);
        } catch (RuntimeException e) {
            logger.error("Unexpected error while parsing document. Document may not"
                    + " match mapping or XML structure.", e);
            throw new ParseException(e);
        }

        return material;
    }

    protected void setIdentifier(Material material, Document doc) {
        Element header = (Element) doc.getElementsByTagName("header").item(0);
        Element identifier = (Element) header.getElementsByTagName("identifier").item(0);
        material.setRepositoryIdentifier(identifier.getTextContent().trim());
    }

    protected void parseVCard(List<Author> authors, String data, AuthorService authorService) {
        if (data.length() > 0) {
            VCard vcard = Ezvcard.parse(data).first();
            String name = vcard.getStructuredName().getGiven();
            String surname = vcard.getStructuredName().getFamily();

            if (name != null && surname != null) {
                Author author = authorService.getAuthorByFullName(name, surname);
                if (author == null) {
                    author = authorService.createAuthor(name, surname);
                    authors.add(author);
                } else if (!authors.contains(author)) {
                    authors.add(author);
                }
            }
        }
    }

    protected List<LanguageString> getLanguageStrings(Node node, LanguageService languageService) {
        List<LanguageString> languageStrings = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            LanguageString languageString = new LanguageString();
            Node currentNode =  nodeList.item(i);

            String text = currentNode.getTextContent().trim();
            if (!text.isEmpty()) {
                languageString.setText(text);

                if (currentNode.hasAttributes()) {
                    String languageCode = currentNode.getAttributes().item(0).getTextContent().trim();
                    String[] tokens = languageCode.split("-");

                    Language language = languageService.getLanguage(tokens[0]);
                    if (language != null) {
                        languageString.setLanguage(language);
                    } else {
                        String message = "No such language for '%s'. LanguageString will have no Language";
                        logger.warn(String.format(message, languageCode));
                    }
                }

                languageStrings.add(languageString);
            }
        }

        return languageStrings;
    }

    protected String getVCardWithNewLines(CharacterData characterData) {
        return characterData.getData().trim().trim().replaceAll("\\n\\s*(?=(\\s*))", "\r\n");
    }

    protected abstract void setAuthors(Material material, Document doc);

    protected abstract void setTaxon(Material material, Document doc);

    protected abstract void setLearningResourceType(Material material, Document doc);

    protected abstract void setTags(Material material, Document doc);

    protected abstract void setSource(Material material, Document doc) throws ParseException;

    protected abstract void setDescriptions(Material material, Document doc);

    protected abstract void setLanguage(Material material, Document doc);

    protected abstract void setTitles(Material material, Document doc) throws ParseException;
}
