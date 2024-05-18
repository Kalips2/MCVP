package com.example.mcvp.repositories;

import com.example.mcvp.models.Artist;
import com.example.mcvp.models.Label;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface LabelRepository extends CrudRepository<Label, Long> {
  Optional<Label> findByName(String name);
}
