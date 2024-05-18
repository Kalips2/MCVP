package com.example.mcvp.repositories;

import com.example.mcvp.models.Album;
import org.springframework.data.repository.CrudRepository;

public interface AlbumRepository extends CrudRepository<Album, Long> {
  Long countAlbumsByLabelName(String labelName);
}
