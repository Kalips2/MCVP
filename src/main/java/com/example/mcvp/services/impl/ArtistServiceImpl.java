package com.example.mcvp.services.impl;

import com.example.mcvp.data.ArtistData;
import com.example.mcvp.exceptions.Exceptions;
import com.example.mcvp.exceptions.InternalException;
import com.example.mcvp.mappers.ArtistMapper;
import com.example.mcvp.mappers.DataMapper;
import com.example.mcvp.models.Artist;
import com.example.mcvp.repositories.ArtistRepository;
import com.example.mcvp.services.ArtistService;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
  private final ArtistRepository artistRepository;

  @Override
  public List<Artist> getAll() {
    return StreamSupport.stream(artistRepository.findAll().spliterator(), false).toList();
  }

  @Override
  public Artist getById(Long id) {
    return artistRepository
        .findById(id)
        .orElseThrow(() -> new InternalException(Exceptions.ARTIST_IS_NOT_FOUND));
  }

  @Override
  public void create(ArtistData artist) throws RuntimeException {
    Artist savedArtist = artistRepository.save(ArtistMapper.dataToEntity(artist));
    log.info("Artist with id = " + savedArtist.getId() + " was saved");
  }

  @Override
  public void update(Long id, ArtistData artist) throws RuntimeException {
    Artist artistToUpdate = artistRepository
        .findById(id)
        .orElseThrow(() -> new InternalException(Exceptions.ARTIST_IS_NOT_FOUND));
    artistToUpdate.setName(artist.getName());
    artistToUpdate.setSurname(artist.getSurname());
    artistToUpdate.setDateOfBirth(DataMapper.dateFromString(artist.getDateOfBirth()));
    artistRepository.save(artistToUpdate);
    log.info("Artist with id = " + id + " was updated");
  }

  @Override
  public void delete(Long id) {
    Artist artist = artistRepository
        .findById(id)
        .orElseThrow(() -> new InternalException(Exceptions.ARTIST_IS_NOT_FOUND));
    artistRepository.delete(artist);
    log.info("Artist with id = " + id + " was deleted");
  }
}
