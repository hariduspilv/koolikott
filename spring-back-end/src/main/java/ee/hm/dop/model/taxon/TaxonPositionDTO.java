package ee.hm.dop.model.taxon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaxonPositionDTO {

    private Long taxonLevelId;
    private String taxonLevel;
    private String taxonLevelName;
}
