package com.example.mcvp.repositories;

import com.example.mcvp.models.Artist;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ArtistRepository implements CrudRepository<Artist, Long> {
  private final Map<Long, Artist> artistStore = new HashMap<>();
  private final AtomicLong idGenerator = new AtomicLong();

  @Override
  public <S extends Artist> S save(S entity) {
    if (entity.getId() == null) {
      entity.setId(idGenerator.incrementAndGet());
    }
    artistStore.put(entity.getId(), entity);
    return entity;
  }

  @Override
  public <S extends Artist> Iterable<S> saveAll(Iterable<S> entities) {
    List<S> result = new ArrayList<>();
    for (S entity : entities) {
      result.add(save(entity));
    }
    return result;
  }

  @Override
  public Optional<Artist> findById(Long id) {
    return Optional.ofNullable(artistStore.get(id));
  }

  @Override
  public boolean existsById(Long id) {
    return artistStore.containsKey(id);
  }

  @Override
  public Iterable<Artist> findAll() {
    return new ArrayList<>(artistStore.values());
  }

  @Override
  public Iterable<Artist> findAllById(Iterable<Long> ids) {
    List<Artist> result = new ArrayList<>();
    for (Long id : ids) {
      Optional.of(artistStore.get(id)).ifPresent(result::add);
    }
    return result;
  }

  @Override
  public long count() {
    return artistStore.size();
  }

  @Override
  public void deleteById(Long id) {
    artistStore.remove(id);
  }

  @Override
  public void delete(Artist entity) {
    artistStore.remove(entity.getId());
  }

  @Override
  public void deleteAllById(Iterable<? extends Long> ids) {
    for (Long id : ids) {
      artistStore.remove(id);
    }
  }

  @Override
  public void deleteAll(Iterable<? extends Artist> entities) {
    for (Artist entity : entities) {
      artistStore.remove(entity.getId());
    }
  }

  @Override
  public void deleteAll() {
    artistStore.clear();
  }
}
