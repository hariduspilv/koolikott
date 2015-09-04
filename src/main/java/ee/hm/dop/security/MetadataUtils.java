package ee.hm.dop.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.provider.DOMMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.security.MetadataCredentialResolver;
import org.opensaml.security.MetadataCredentialResolverFactory;
import org.opensaml.security.MetadataCriteria;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.x509.X509Credential;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MetadataUtils {

    public static X509Credential getCerdential(String credentialPath) throws Exception {
        DocumentBuilder docBuilder = getDocumentBuilder();

        Element metadataRoot = getElement(credentialPath, docBuilder);

        MetadataCredentialResolver credentialResolver = getMetadataCredentialResolver(metadataRoot);

        CriteriaSet criteriaSet = getCriterias();

        return (X509Credential) credentialResolver.resolveSingle(criteriaSet);
    }

    private static CriteriaSet getCriterias() {
        CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.add(new MetadataCriteria(IDPSSODescriptor.DEFAULT_ELEMENT_NAME, SAMLConstants.SAML20P_NS));
        criteriaSet.add(new EntityIDCriteria("https://reos.taat.edu.ee/saml2/idp/metadata.php"));
        return criteriaSet;
    }

    private static MetadataCredentialResolver getMetadataCredentialResolver(Element metadataRoot)
            throws MetadataProviderException {
        DOMMetadataProvider idpMetadataProvider = new DOMMetadataProvider(metadataRoot);
        idpMetadataProvider.setRequireValidMetadata(true);
        idpMetadataProvider.setParserPool(new BasicParserPool());
        idpMetadataProvider.initialize();

        MetadataCredentialResolverFactory credentialResolverFactory = MetadataCredentialResolverFactory.getFactory();

        return credentialResolverFactory.getInstance(idpMetadataProvider);
    }

    private static Element getElement(String credentialPath, DocumentBuilder docBuilder) throws Exception {
        InputStream metaDataInputStream = new FileInputStream(getResourceAsFile(credentialPath));
        Document metaDataDocument = docBuilder.parse(metaDataInputStream);
        Element metadataRoot = metaDataDocument.getDocumentElement();
        metaDataInputStream.close();
        return metadataRoot;
    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        return documentBuilderFactory.newDocumentBuilder();
    }

    protected static File getResourceAsFile(String resourcePath) throws URISyntaxException {
        URI resource = MetadataUtils.class.getClassLoader().getResource(resourcePath).toURI();
        return new File(resource);
    }
}
