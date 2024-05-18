package com.example.mcvp.repositories.impl;

import com.example.mcvp.models.Label;
import com.example.mcvp.repositories.LabelRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public class LabelRepositoryImpl implements LabelRepository {
  private final Map<Long, Label> labelStore = new HashMap<>();
  private final AtomicLong idGenerator = new AtomicLong();

  @Override
  public <S extends Label> S save(S entity) {
    if (entity.getId() == null) {
      entity.setId(idGenerator.incrementAndGet());
    }
    labelStore.put(entity.getId(), entity);
    return entity;
  }

  @Override
  public <S extends Label> Iterable<S> saveAll(Iterable<S> entities) {
    List<S> result = new ArrayList<>();
    for (S entity : entities) {
      result.add(save(entity));
    }
    return result;
  }

  @Override
  public Optional<Label> findById(Long id) {
    return Optional.ofNullable(labelStore.get(id));
  }

  @Override
  public boolean existsById(Long id) {
    return labelStore.containsKey(id);
  }

  @Override
  public Iterable<Label> findAll() {
    return new ArrayList<>(labelStore.values());
  }

  @Override
  public Iterable<Label> findAllById(Iterable<Long> ids) {
    List<Label> result = new ArrayList<>();
    for (Long id : ids) {
      Optional.of(labelStore.get(id)).ifPresent(result::add);
    }
    return result;
  }

  @Override
  public long count() {
    return labelStore.size();
  }

  @Override
  public void deleteById(Long id) {
    labelStore.remove(id);
  }

  @Override
  public void delete(Label entity) {
    labelStore.remove(entity.getId());
  }

  @Override
  public void deleteAllById(Iterable<? extends Long> ids) {
    for (Long id : ids) {
      labelStore.remove(id);
    }
  }

  @Override
  public void deleteAll(Iterable<? extends Label> entities) {
    for (Label entity : entities) {
      labelStore.remove(entity.getId());
    }
  }

  @Override
  public void deleteAll() {
    labelStore.clear();
  }

  public Optional<Label> findByName(String name) {
    return labelStore.values().stream()
        .filter(label -> label.getName().equalsIgnoreCase(name))
        .findFirst();
  }
}
