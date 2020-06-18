package ee.hm.dop.common.test;

import ee.hm.dop.model.Material;
import org.json.JSONArray;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class Material1LdJsonValidation {

    private static final String sdStringExpected = "{\n" +
            "      \"@type\": \"Organization\",\n" +
            "      \"name\": \"HITSA\"\n" +
            "  }";

    private static final String publisherExpected = "{\n" +
            "      \"@type\": \"Organization\",\n" +
            "      \"name\": \"Koolibri\"\n" +
            "  }";

    private static final String authorExpected = "{\n" +
            "      \"@type\": \"Person\",\n" +
            "      \"givenName\": \"Isaac\",\n" +
            "      \"familyName\": \"John Newton\",\n" +
            "      \"name\": \"Isaac John Newton\"\n" +
            "  }";

    public static void assertMaterial1(String ldJson, Material material) {
        JSONArray jsonArray = new JSONArray(ldJson);
        IntStream.range(0, jsonArray.length()).
                mapToObj(jsonArray::getJSONObject).
                forEach(jsonObject -> assertJson(jsonObject, material));
    }

    private static void assertReview(JSONObject jsonObject) {
    }

    private static void assertBreadCrumbList(JSONObject jsonObject) {
    }

    private static void assertOrganization(JSONObject jsonObject) {
        String expected = "{\n" +
                "    \"@context\": \"http://schema.org/\",\n" +
                "    \"@type\": \"Organization\",\n" +
                "    \"url\": \"https://www.e-koolikott.ee/\",\n" +
                "    \"logo\": \"https://e-koolikott.ee/ekoolikott.png\"\n" +
                "  }";
        JSONAssert.assertEquals(expected, jsonObject, JSONCompareMode.STRICT);
    }

    private static void assertCreativeWork(JSONObject jsonObject, Material material) {

        assertEquals(jsonObject.get("@id"), "https://www.youtube.com/watch?v=gSWbx3CvVUk");
        assertEquals(jsonObject.get("@context"), "http://schema.org/");
        assertEquals(jsonObject.get("@type"), "CreativeWork");
        assertEquals(jsonObject.get("dateCreated"), "1999-01-01");
        assertEquals(jsonObject.get("datePublished"), "1983-02-02");
        assertEquals(jsonObject.get("interactionCount"), "100 UserPageVisits");
        assertEquals(jsonObject.get("inLanguage"), "et");
        assertEquals(jsonObject.get("license"), "http://creativecommons.org/licenses/by/4.0/");
        assertEquals(((JSONObject) jsonObject.get("publisher")).getString("name"), "Koolibri");

        JSONAssert.assertEquals(sdStringExpected, jsonObject.get("sdPublisher").toString(), JSONCompareMode.STRICT);
        JSONAssert.assertEquals(publisherExpected, jsonObject.get("publisher").toString(), JSONCompareMode.STRICT);

        JSONAssert.assertEquals(authorExpected, ((JSONArray) jsonObject.get("author")).get(0).toString(), JSONCompareMode.LENIENT);

        assertEquals(((JSONArray) jsonObject.get("learningResourceType")).length(), material.getResourceTypes().size());
        assertEquals(((JSONArray) jsonObject.get("about")).length(), material.getTaxonPositionDto().size());//TODO
        assertEquals(((JSONArray) jsonObject.get("keywords")).length(), material.getTags().size());
        assertEquals(((JSONArray) jsonObject.get("abstract")).length(), material.getDescriptions().size());
        assertEquals(((JSONArray) jsonObject.get("headline")).length(), material.getTitles().size());
        assertEquals(((JSONArray) jsonObject.get("author")).length(), material.getAuthors().size());
    }

    private static void assertWebSite(JSONObject jsonObject) {
        String expectedWebSite = " {\n" +
                "    \"@context\": \"http://schema.org/\",\n" +
                "    \"@type\": \"WebSite\",\n" +
                "    \"url\": \"https://www.e-koolikott.ee/\",\n" +
                "    \"potentialAction\": {\n" +
                "      \"@type\": \"SearchAction\",\n" +
                "      \"target\": \"https://e-koolikott.ee/search/result?q={search_term_string}\",\n" +
                "      \"query-input\": \"required name=search_term_string\"\n" +
                "    }\n" +
                "  },";

        JSONAssert.assertEquals(expectedWebSite, jsonObject, JSONCompareMode.STRICT);
    }

    private static void assertJson(JSONObject jsonObject, Material material) {
        if (jsonObject.get("@type").equals("WebSite")) {
            assertWebSite(jsonObject);
        }
        if (jsonObject.get("@type").equals("CreativeWork")) {
            assertCreativeWork(jsonObject, material);
        }
        if (jsonObject.get("@type").equals("Organization")) {
            assertOrganization(jsonObject);
        }
        if (jsonObject.get("@type").equals("BreadcrumbList")) {
            assertBreadCrumbList(jsonObject);
        }
        if (jsonObject.get("@type").equals("Review")) {
            assertReview(jsonObject);
        }
    }
}
