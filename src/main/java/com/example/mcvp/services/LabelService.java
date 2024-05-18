package com.example.mcvp.services;

import com.example.mcvp.data.LabelData;
import com.example.mcvp.models.Label;
import java.util.List;
import java.util.Optional;

public interface LabelService {
  List<Label> getAll();

  Label getById(Long id);

  Long create(LabelData label) throws RuntimeException;

  void add(LabelData... labelData) throws RuntimeException;

  void update(Long id, LabelData label) throws RuntimeException;

  void delete(Long id);

  Optional<Label> getByName(String name);

  List<String> getNamesThatContain(String name);
}
