package ee.hm.dop.rest.sisuloome;

import ee.hm.dop.model.enums.Visibility;
import java.math.BigInteger;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SisuloomeMaterial {

  String personalCode;
  BigInteger externalMaterialId;
  String addedAt;
  Visibility visibility;
  String title;
  String materialUrl;
  String embedCode;
  List<SisuloomeMaterialAuthor> authors;
}
