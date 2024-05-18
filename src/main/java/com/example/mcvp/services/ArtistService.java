package com.example.mcvp.services;


import com.example.mcvp.data.ArtistData;
import com.example.mcvp.models.Artist;
import java.util.List;

public interface ArtistService {
  List<Artist> getAll();

  Artist getById(Long id);

  void create(ArtistData artist) throws RuntimeException;

  void update(Long id, ArtistData artist) throws RuntimeException;

  void delete(Long id);

}
