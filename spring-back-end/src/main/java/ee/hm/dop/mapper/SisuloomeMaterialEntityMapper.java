package ee.hm.dop.mapper;

import ee.hm.dop.model.sisuloome.SisuloomeMaterialEntity;
import ee.hm.dop.rest.sisuloome.SisuloomeMaterialResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfiguration.class, uses = SisuloomeMaterialAuthorMapper.class)
public interface SisuloomeMaterialEntityMapper {

  @Mapping(source = "id", target = "sisuloomeMaterialId")
  SisuloomeMaterialResponse toResponse(SisuloomeMaterialEntity sisuloomeMaterialEntity);
}
