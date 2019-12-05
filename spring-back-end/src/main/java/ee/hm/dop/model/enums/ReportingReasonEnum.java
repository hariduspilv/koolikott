package ee.hm.dop.model.enums;

import java.util.Arrays;
import java.util.List;

public enum ReportingReasonEnum {
    LO_CONTENT, LO_FORM, LO_METADATA, REPORTING_REASON_COMMENT, REPORTING_REASON_TAG, LO_COPYRIGHT, LO_OTHER ;

    public static List<ReportingReasonEnum> learningObjectReportingReasonsModal(){
        return Arrays.asList(LO_CONTENT, LO_COPYRIGHT, LO_FORM, LO_OTHER);
    }

    public static List<ReportingReasonEnum> tagReportingReasons(){
        return Arrays.asList(REPORTING_REASON_TAG);
    }

    public static List<ReportingReasonEnum> commentReportingReasons(){
        return Arrays.asList(REPORTING_REASON_COMMENT);
    }
}
