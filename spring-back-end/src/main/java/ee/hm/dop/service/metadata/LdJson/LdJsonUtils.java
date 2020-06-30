package ee.hm.dop.service.metadata.LdJson;

import ee.hm.dop.model.IssueDate;
import ee.hm.dop.model.Material;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class LdJsonUtils {
    static final Map<String, ArrayList<String>> definedGradeGroups = new HashMap<>();
    public static final String ENGLISH = "English";
    public static final String KLASS = ".klass";
    public static final String RUSSIAN = "Russian";
    public static final String REGEX_ONLY_LETTERS = "[^A-Za-z]+";
    public static final String EN = "en";
    public static final String RU = "ru";
    public static final String ET = "et";
    public static final String EMPTY_STRING = "";

    static {
        definedGradeGroups.put("PRESCHOOL", newArrayList("ZERO_FIVE", "SIX_SEVEN"));
        definedGradeGroups.put("LEVEL1", newArrayList("GRADE1", "GRADE2", "GRADE3"));
        definedGradeGroups.put("LEVEL2", newArrayList("GRADE4", "GRADE5", "GRADE6"));
        definedGradeGroups.put("LEVEL3", newArrayList("GRADE7", "GRADE8", "GRADE9"));
    }

    static String convertLanguages(Material material) {
        if (material.getLanguage() != null && material.getLanguage().getName() != null) {
            if (material.getLanguage().getName().equalsIgnoreCase(ENGLISH)) {
                return EN;
            }
            if (material.getLanguage().getName().equalsIgnoreCase(RUSSIAN)) {
                return RU;
            }
        }
        return ET;
    }

    static String createDateCreated(Material material) {
        return material.getAdded() != null ? material.getAdded().toLocalDate().toString() : EMPTY_STRING;
    }

    static LocalDate convertIssueDateToLocalDate(IssueDate issueDate) {
        return LocalDate.of(issueDate.getYear(), issueDate.getMonth(), issueDate.getDay());
    }

    static String translateEducationalContext(String eduContext) {
        String translation = null;
        switch (eduContext) {
            case ("SECONDARYEDUCATION"):
                translation = "Keskkooliõpilased";
                break;
            case ("BASICEDUCATION"):
                translation = "Põhikooliõpilased";
                break;
            case ("PRESCHOOLEDUCATION"):
                translation = "Lasteaialapsed";
                break;
            case ("VOCATIONALEDUCATION"):
                translation = "Kutsekooliõpilased";
                break;
            case ("NONFORMALEDUCATION"):
                translation = "Mitteformaalne_õpe";
                break;
        }
        return translation;
    }

    static String translateLearningResourceType(String resourceType) {
        String translation;
        switch (resourceType) {
            case ("BOOKMARKSHARINGPLATFORM"):
                translation = "bookmark sharing platform";
                break;
            case ("DRILLANDPRACTICE"):
                translation = "drill and practice";
                break;
            case ("EDUCATIONALGAME"):
                translation = "educational game";
                break;
            case ("ENQUIRYORIENTEDACTIVITY"):
                translation = "enquiry oriented activity";
                break;
            case ("IMAGESHARINGPLATFORM"):
                translation = "image sharing platform";
                break;
            case ("OPENACTIVITY"):
                translation = "open activity";
                break;
            case ("REFERENCESHARINGPLATFORM"):
                translation = "reference sharing platform";
                break;
            case ("SOUNDSHARINGPLATFORM"):
                translation = "sound sharing platform";
                break;
            case ("VIDEOSHARINGPLATFORM"):
                translation = "video sharing platform";
                break;
            default:
                translation = resourceType.toLowerCase();
                break;
        }
        return translation;
    }

    static String generalGradeLimits(String level) {
        String generalGrade = null;
        switch (level) {
            case ("PRESCHOOL"):
                generalGrade = "0-7";
                break;
            case ("LEVEL1"):
                generalGrade = "7-10";
                break;
            case ("LEVEL2"):
                generalGrade = "10-13";
                break;
            case ("LEVEL3"):
                generalGrade = "13-16";
                break;
        }
        return generalGrade;
    }

    static String translateGradeLimits(String level) {
        String generalGrade = null;
        switch (level) {
            case ("ZERO_FIVE"):
            case ("SIX_SEVEN"):
            case ("PRESCHOOL"):
                generalGrade = "Koolieelne_õppeasutus";
                break;
            case ("LEVEL1"):
                generalGrade = "I_kooliaste";
                break;
            case ("LEVEL2"):
                generalGrade = "II_kooliaste";
                break;
            case ("LEVEL3"):
                generalGrade = "III_kooliaste";
                break;
            case ("GYMNASIUM"):
                generalGrade = "Gümnaasium";
                break;
        }
        return generalGrade;
    }

    static String translateClassesLimit(String level) {
        return level.replaceAll("\\D+", EMPTY_STRING) + KLASS;
    }

    static String findLicenseType(String license) {
        String licenseLink;
        String licenceWithoutNumbers = license.replaceAll(REGEX_ONLY_LETTERS, EMPTY_STRING);

        switch (licenceWithoutNumbers) {
            case ("CCBY"):
                licenseLink = "http://creativecommons.org/licenses/by/4.0/";
                break;
            case ("CCBYNC"):
                licenseLink = "https://creativecommons.org/licenses/by-nc/4.0/";
                break;
            case ("CCBYNCND"):
                licenseLink = "https://creativecommons.org/licenses/by-nc-nd/4.0/";
                break;
            case ("CCBYNCSA"):
                licenseLink = "https://creativecommons.org/licenses/by-nc-sa/4.0/";
                break;
            case ("CCBYND"):
                licenseLink = "https://creativecommons.org/licenses/by-nd/4.0/";
                break;
            case ("CCBYSA"):
                licenseLink = "https://creativecommons.org/licenses/by-sa/4.0/";
                break;
            case ("Youtube"):
                licenseLink = "Youtube";
                break;
            case ("allRightsReserved"):
                licenseLink = "Kõik õigused kaitstud";
                break;
            default:
                licenseLink = EMPTY_STRING;
                break;
        }
        return licenseLink;
    }

    static String typicalAgeRanges(String grade) {
        String ageRange = EMPTY_STRING;
        switch (grade) {
            case ("ZERO_FIVE"):
                ageRange = "0-5";
                break;
            case "SIX_SEVEN":
                ageRange = "6-7";
                break;
            case "GRADE1":
                ageRange = "7-8";
                break;
            case "GRADE2":
                ageRange = "8-9";
                break;
            case "GRADE3":
                ageRange = "9-10";
                break;
            case "GRADE4":
                ageRange = "10-11";
                break;
            case "GRADE5":
                ageRange = "11-12";
                break;
            case "GRADE6":
                ageRange = "12-13";
                break;
            case "GRADE7":
                ageRange = "13-14";
                break;
            case "GRADE8":
                ageRange = "14-15";
                break;
            case "GRADE9":
                ageRange = "15-16";
                break;
            case ("GYMNASIUM"):
            case ("LEVEL_GYMNASIUM"):
                ageRange = "16-19";
                break;
        }
        return ageRange;
    }

}

