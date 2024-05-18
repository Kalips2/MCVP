package com.example.mcvp.repositories.impl;

import com.example.mcvp.models.Genre;
import com.example.mcvp.models.Song;
import com.example.mcvp.repositories.SongRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SongRepositoryImpl implements SongRepository {
  private final Map<Long, Song> songStore = new HashMap<>();
  private final AtomicLong idGenerator = new AtomicLong();

  @Override
  public <S extends Song> S save(S entity) {
    if (entity.getId() == null) {
      entity.setId(idGenerator.incrementAndGet());
    }
    songStore.put(entity.getId(), entity);
    return entity;
  }

  @Override
  public <S extends Song> Iterable<S> saveAll(Iterable<S> entities) {
    List<S> result = new ArrayList<>();
    for (S entity : entities) {
      result.add(save(entity));
    }
    return result;
  }

  @Override
  public Optional<Song> findById(Long id) {
    return Optional.ofNullable(songStore.get(id));
  }

  @Override
  public boolean existsById(Long id) {
    return songStore.containsKey(id);
  }

  @Override
  public Iterable<Song> findAll() {
    return new ArrayList<>(songStore.values());
  }

  @Override
  public Iterable<Song> findAllById(Iterable<Long> ids) {
    List<Song> result = new ArrayList<>();
    for (Long id : ids) {
      Optional.of(songStore.get(id)).ifPresent(result::add);
    }
    return result;
  }

  @Override
  public long count() {
    return songStore.size();
  }

  @Override
  public void deleteById(Long id) {
    songStore.remove(id);
  }

  @Override
  public void delete(Song entity) {
    songStore.remove(entity.getId());
  }

  @Override
  public void deleteAllById(Iterable<? extends Long> ids) {
    for (Long id : ids) {
      songStore.remove(id);
    }
  }

  @Override
  public void deleteAll(Iterable<? extends Song> entities) {
    for (Song entity : entities) {
      songStore.remove(entity.getId());
    }
  }

  @Override
  public void deleteAll() {
    songStore.clear();
  }

  public long countSongsByGenre(Genre genre) {
    return songStore.values().stream()
        .filter(song -> song.getGenre() != null && song.getGenre().equals(genre))
        .count();
  }
}
