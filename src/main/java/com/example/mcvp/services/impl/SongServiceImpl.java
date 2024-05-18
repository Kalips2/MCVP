package com.example.mcvp.services.impl;

import com.example.mcvp.data.SongData;
import com.example.mcvp.exceptions.Exceptions;
import com.example.mcvp.exceptions.InternalException;
import com.example.mcvp.mappers.SongMapper;
import com.example.mcvp.models.Album;
import com.example.mcvp.models.Genre;
import com.example.mcvp.models.Song;
import com.example.mcvp.repositories.AlbumRepository;
import com.example.mcvp.repositories.SongRepository;
import com.example.mcvp.repositories.impl.AlbumRepositoryImpl;
import com.example.mcvp.repositories.impl.SongRepositoryImpl;
import com.example.mcvp.services.SongService;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {
  private final SongRepository songRepository;
  private final AlbumRepository albumRepository;

  @Override
  public List<Song> getAll() {
    return StreamSupport.stream(songRepository.findAll().spliterator(), false).toList();
  }

  @Override
  public Song getById(Long id) {
    return songRepository
        .findById(id)
        .orElseThrow(() -> new InternalException(Exceptions.SONG_IS_NOT_FOUND));
  }

  @Override
  public void create(SongData song) throws RuntimeException {
    Album album = albumRepository
        .findById(song.getAlbum_id())
        .orElseThrow(() -> new InternalException(Exceptions.ALBUM_IS_NOT_FOUND));
    Song savedSong = songRepository.save(SongMapper.dataToEntity(song, album));
    log.info("Song with id = " + savedSong.getId() + " was saved");
  }

  @Override
  public void update(Long id, SongData song) throws RuntimeException {
    Song songToUpdate = songRepository
        .findById(id)
        .orElseThrow(() -> new InternalException(Exceptions.SONG_IS_NOT_FOUND));
    songToUpdate.setTitle(song.getTitle());
    songToUpdate.setDuration(song.getDuration());
    songToUpdate.setGenre(song.getGenre());
    Album album = albumRepository
        .findById(song.getAlbum_id())
        .orElseThrow(() -> new InternalException(Exceptions.ALBUM_IS_NOT_FOUND));
    songToUpdate.setAlbum(album);
    songRepository.save(songToUpdate);
    log.info("Song with id = " + id + " was updated");
  }

  @Override
  public void delete(Long id) {
    Song song = songRepository
        .findById(id)
        .orElseThrow(() -> new InternalException(Exceptions.SONG_IS_NOT_FOUND));
    songRepository.delete(song);
    log.info("Song with id = " + id + " was deleted");
  }

  @Override
  public long countSongsByGenre(Genre genre) {
    return songRepository.countSongsByGenre(genre);
  }
}
