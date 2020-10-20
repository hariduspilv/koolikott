package ee.hm.dop.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingInheritanceStrategy.AUTO_INHERIT_ALL_FROM_CONFIG;

import org.mapstruct.MapperConfig;

@MapperConfig(
    componentModel = "spring",
    injectionStrategy = CONSTRUCTOR,
    mappingInheritanceStrategy = AUTO_INHERIT_ALL_FROM_CONFIG
)
public interface MapperConfiguration {

}