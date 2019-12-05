package ee.hm.dop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ImproperContentDto {

    private Long id;
    private boolean reviewed = false;
    private String reportingText;
    private List<ReportingReason> reportingReasons;

}
