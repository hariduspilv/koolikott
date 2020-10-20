package ee.hm.dop.mapper;

import ee.hm.dop.model.sisuloome.SisuloomeMaterialAuthorEntity;
import ee.hm.dop.rest.sisuloome.SisuloomeMaterialAuthor;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class)
public interface SisuloomeMaterialAuthorMapper {

  SisuloomeMaterialAuthorEntity toEntity(SisuloomeMaterialAuthor sisuloomeMaterialAuthor);
}
