package com.example.mcvp.repositories;

import com.example.mcvp.models.Artist;
import com.example.mcvp.models.Genre;
import com.example.mcvp.models.Song;
import org.springframework.data.repository.CrudRepository;

public interface SongRepository extends CrudRepository<Song, Long> {
  long countSongsByGenre(Genre genre);
}
