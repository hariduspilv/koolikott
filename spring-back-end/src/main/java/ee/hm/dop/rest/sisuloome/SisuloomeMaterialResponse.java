package ee.hm.dop.rest.sisuloome;

import java.math.BigInteger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SisuloomeMaterialResponse {

  private final BigInteger sisuloomeMaterialId;
  private final BigInteger externalMaterialId;
}
