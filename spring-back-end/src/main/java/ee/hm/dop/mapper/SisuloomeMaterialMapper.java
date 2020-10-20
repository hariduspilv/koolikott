package ee.hm.dop.mapper;

import ee.hm.dop.model.sisuloome.SisuloomeMaterialEntity;
import ee.hm.dop.rest.sisuloome.SisuloomeMaterial;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class, uses = SisuloomeMaterialAuthorMapper.class)
public interface SisuloomeMaterialMapper {

  SisuloomeMaterialEntity toEntity(SisuloomeMaterial sisuloomeMaterial);
}
