package ee.hm.dop.model.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EducationalContextC {
    public static final String PRESCHOOLEDUCATION = "PRESCHOOLEDUCATION";
    public static final String BASICEDUCATION = "BASICEDUCATION";
    public static final String SECONDARYEDUCATION = "SECONDARYEDUCATION";
    public static final String VOCATIONALEDUCATION = "VOCATIONALEDUCATION";

    public static final List<String> ALL = Collections.unmodifiableList(Arrays.asList(PRESCHOOLEDUCATION, BASICEDUCATION, SECONDARYEDUCATION, VOCATIONALEDUCATION));
    public static final List<String> BASIC_AND_SECONDARY = Collections.unmodifiableList(Arrays.asList(BASICEDUCATION, SECONDARYEDUCATION));
}
