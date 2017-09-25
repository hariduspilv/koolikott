package ee.hm.dop.service;

public enum SuggestionStrategy {
    SUGGEST_TAG,
    SUGGEST_URL;

    public boolean suggestTag(){
        return this == SUGGEST_TAG;
    }
}
