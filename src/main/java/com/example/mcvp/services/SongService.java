package com.example.mcvp.services;

import com.example.mcvp.data.SongData;
import com.example.mcvp.models.Genre;
import com.example.mcvp.models.Song;
import java.util.List;

public interface SongService {
  List<Song> getAll();

  Song getById(Long id);

  void create(SongData song) throws RuntimeException;

  void update(Long id, SongData song) throws RuntimeException;

  void delete(Long id);

  long countSongsByGenre(Genre genre);
}
