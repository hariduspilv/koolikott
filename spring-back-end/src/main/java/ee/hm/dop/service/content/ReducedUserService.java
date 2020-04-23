package ee.hm.dop.service.content;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.hm.dop.model.Views;
import org.springframework.stereotype.Service;

@Service
public class ReducedUserService {

    public ObjectMapper getMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        objectMapper.setConfig(objectMapper.getSerializationConfig().withView(Views.publicUser.class));
        return objectMapper;
    }

}
