package ee.hm.dop.rest.sisuloome;

import static org.springframework.http.ResponseEntity.ok;

import ee.hm.dop.dao.sisuloome.SisuloomeMaterialDao;
import ee.hm.dop.mapper.SisuloomeMaterialEntityMapper;
import ee.hm.dop.mapper.SisuloomeMaterialMapper;
import ee.hm.dop.model.sisuloome.SisuloomeMaterialEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("sisuloome")
public class SisuloomeMaterialResource {

  private final SisuloomeMaterialDao sisuloomeMaterialDao;
  private final SisuloomeMaterialMapper sisuloomeMaterialMapper;
  private final SisuloomeMaterialEntityMapper sisuloomeMaterialEntityMapper;

  @PostMapping
  public ResponseEntity<SisuloomeMaterialResponse> postSisuloomeMaterial(@RequestBody SisuloomeMaterial sisuloomeMaterial) {
    SisuloomeMaterialEntity entity = sisuloomeMaterialDao.createOrUpdate(sisuloomeMaterialMapper.toEntity(sisuloomeMaterial));
    return entity != null ? ok(sisuloomeMaterialEntityMapper.toResponse(entity)) : ResponseEntity.badRequest().build();
  }
}
