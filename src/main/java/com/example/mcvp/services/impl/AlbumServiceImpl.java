package com.example.mcvp.services.impl;

import com.example.mcvp.data.AlbumData;
import com.example.mcvp.exceptions.Exceptions;
import com.example.mcvp.exceptions.InternalException;
import com.example.mcvp.mappers.AlbumMapper;
import com.example.mcvp.mappers.DataMapper;
import com.example.mcvp.models.Album;
import com.example.mcvp.models.Artist;
import com.example.mcvp.models.Label;
import com.example.mcvp.repositories.AlbumRepository;
import com.example.mcvp.repositories.ArtistRepository;
import com.example.mcvp.repositories.LabelRepository;
import com.example.mcvp.services.AlbumService;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
  private final AlbumRepository albumRepository;
  private final ArtistRepository artistRepository;
  private final LabelRepository labelRepository;

  @Override
  public List<Album> getAll() {
    return StreamSupport.stream(albumRepository.findAll().spliterator(), false).toList();
  }

  @Override
  public Album getById(Long id) {
    return albumRepository
        .findById(id)
        .orElseThrow(() -> new InternalException(Exceptions.ALBUM_IS_NOT_FOUND));
  }

  @Override
  public void create(AlbumData album, MultipartFile file) throws RuntimeException {
    Artist artist = artistRepository
        .findById(album.getArtistId())
        .orElseThrow(() -> new InternalException(Exceptions.ARTIST_IS_NOT_FOUND));
    Label label = labelRepository
        .findById(album.getLabelId())
        .orElseThrow(() -> new InternalException(Exceptions.LABEL_IS_NOT_FOUND));
    Album savedAlbum = albumRepository.save(AlbumMapper.dataToEntity(album, file, artist, label));
    log.info("Album with id = " + savedAlbum.getId() + " was saved");
  }

  @Override
  public void update(Long id, AlbumData album, MultipartFile file) throws RuntimeException {
    Album albumToUpdate = albumRepository
        .findById(id)
        .orElseThrow(() -> new InternalException(Exceptions.ALBUM_IS_NOT_FOUND));
    albumToUpdate.setTitle(album.getTitle());

    albumToUpdate.setReleaseDate(DataMapper.dateFromString(album.getReleaseDate()));

    Artist newArtist = artistRepository
        .findById(album.getArtistId())
        .orElseThrow(() -> new InternalException(Exceptions.ARTIST_IS_NOT_FOUND));
    albumToUpdate.setArtist(newArtist);

    Label label = labelRepository
        .findById(album.getLabelId())
        .orElseThrow(() -> new InternalException(Exceptions.LABEL_IS_NOT_FOUND));
    albumToUpdate.setLabel(label);

    albumToUpdate.setPathToPhoto("Updated photo path " + file.getName());

    albumRepository.save(albumToUpdate);
    log.info("Album with id = " + id + " was updated");
  }

  @Override
  public void delete(Long id) {
    Album albumToDelete = albumRepository
        .findById(id)
        .orElseThrow(() -> new InternalException(Exceptions.ALBUM_IS_NOT_FOUND));
    albumRepository.delete(albumToDelete);
    log.info("Album with id = " + id + " was deleted");
  }

  @Override
  public Long countAlbumsByLabelName(String labelName) {
    return albumRepository.countAlbumsByLabelName(labelName);
  }
}
