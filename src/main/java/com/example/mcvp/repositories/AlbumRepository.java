package com.example.mcvp.repositories;

import com.example.mcvp.models.Album;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class AlbumRepository implements CrudRepository<Album, Long> {
  private final Map<Long, Album> albumStore = new HashMap<>();
  private final AtomicLong idGenerator = new AtomicLong();

  @Override
  public <S extends Album> S save(S entity) {
    if (entity.getId() == null) {
      entity.setId(idGenerator.incrementAndGet());
    }
    albumStore.put(entity.getId(), entity);
    return entity;
  }

  @Override
  public <S extends Album> Iterable<S> saveAll(Iterable<S> entities) {
    List<S> result = new ArrayList<>();
    for (S entity : entities) {
      result.add(save(entity));
    }
    return result;
  }

  @Override
  public Optional<Album> findById(Long id) {
    return Optional.ofNullable(albumStore.get(id));
  }

  @Override
  public boolean existsById(Long id) {
    return albumStore.containsKey(id);
  }

  @Override
  public Iterable<Album> findAll() {
    return new ArrayList<>(albumStore.values());
  }

  @Override
  public Iterable<Album> findAllById(Iterable<Long> ids) {
    List<Album> result = new ArrayList<>();
    for (Long id : ids) {
      Optional.of(albumStore.get(id)).ifPresent(result::add);
    }
    return result;
  }

  @Override
  public long count() {
    return albumStore.size();
  }

  @Override
  public void deleteById(Long id) {
    albumStore.remove(id);
  }

  @Override
  public void delete(Album entity) {
    albumStore.remove(entity.getId());
  }

  @Override
  public void deleteAllById(Iterable<? extends Long> ids) {
    for (Long id : ids) {
      albumStore.remove(id);
    }
  }

  @Override
  public void deleteAll(Iterable<? extends Album> entities) {
    for (Album entity : entities) {
      albumStore.remove(entity.getId());
    }
  }

  @Override
  public void deleteAll() {
    albumStore.clear();
  }

  public Long countAlbumsByLabelName(String labelName) {
    return albumStore.values().stream()
        .filter(album -> album.getLabel() != null && album.getLabel().getName().equals(labelName))
        .count();
  }
}
