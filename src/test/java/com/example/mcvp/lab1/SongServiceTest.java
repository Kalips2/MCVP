package com.example.mcvp.lab1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.example.mcvp.data.SongData;
import com.example.mcvp.exceptions.Exceptions;
import com.example.mcvp.exceptions.InternalException;
import com.example.mcvp.mappers.DataMapper;
import com.example.mcvp.models.Album;
import com.example.mcvp.models.Genre;
import com.example.mcvp.models.Song;
import com.example.mcvp.repositories.impl.AlbumRepositoryImpl;
import com.example.mcvp.repositories.impl.SongRepositoryImpl;
import com.example.mcvp.services.impl.SongServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SongServiceTest {
  static SongRepositoryImpl songRepository;
  static AlbumRepositoryImpl albumRepository;

  static List<Song> testSongs;
  static List<Album> testAlbums;

  static SongServiceImpl songService;

  @BeforeAll
  public static void setupAll() {
    albumRepository = new AlbumRepositoryImpl();
    songRepository = new SongRepositoryImpl();
    songService = new SongServiceImpl(songRepository, albumRepository);
  }

  @BeforeEach
  public void setupTest() {
    resetElements();

    List<Album> savedAlbums = StreamSupport.stream(albumRepository.saveAll(testAlbums).spliterator(), false).toList();

    for (int i = 0; i < testSongs.size(); i++) {
      Song song = testSongs.get(i);
      song.setAlbum(savedAlbums.get(i % savedAlbums.size()));
      songRepository.save(song);
    }
  }

  private void resetElements() {
    albumRepository.deleteAll();
    songRepository.deleteAll();

    testAlbums = Arrays.asList(
        new Album(null, "Album1", DataMapper.dateFromString("2023-10-10"), "path/to/photo/1", null, null),
        new Album(null, "Album2", DataMapper.dateFromString("2022-10-10"), "path/to/photo/2", null, null)
    );

    testSongs = Arrays.asList(
        new Song(null, "Song1", 3.5, Genre.ROCK, null),
        new Song(null, "Song2", 4.0, Genre.POP, null),
        new Song(null, "Song3", 5.0, Genre.JAZZ, null),
        new Song(null, "Song4", 3.0, Genre.CLASSICAL, null),
        new Song(null, "Song5", 4.5, Genre.ROCK, null)
    );
  }

  @Test
  public void testGetAll() {
    List<Song> result = songService.getAll();

    assumeTrue(!result.isEmpty());

    assertAll(
        () -> assertThat(result, hasSize(5)),
        () -> assertThat(result, containsInAnyOrder(
            hasProperty("title", is("Song1")),
            hasProperty("title", is("Song2")),
            hasProperty("title", is("Song3")),
            hasProperty("title", is("Song4")),
            hasProperty("title", is("Song5"))
        )),
        () -> assertThat(result.stream().map(Song::getAlbum).collect(Collectors.toList()), hasItems(
            hasProperty("title", is("Album1")),
            hasProperty("title", is("Album2"))
        ))
    );
  }

  @Test
  public void testGetById() {
    Song song = songService.getById(testSongs.get(0).getId());

    assertAll(
        () -> assertNotNull(song),
        () -> assertThat(song.getTitle(), is("Song1")),
        () -> assertThat(song.getAlbum().getTitle(), is("Album1"))
    );
  }

  @Test
  public void testGetByIdThrowException() {
    Exception exception = assertThrows(InternalException.class, () -> songService.getById(999L));

    assertThat(exception.getMessage(), is(Exceptions.SONG_IS_NOT_FOUND.getMessage()));
  }

  @Test
  public void testCreate() {
    SongData newSongData = new SongData("New Song", 4.0, Genre.ROCK, testAlbums.get(0).getId());
    songService.create(newSongData);

    List<Song> result = songService.getAll();
    assertThat(result, hasSize(6));

    Song createdSong = result.stream()
        .filter(s -> s.getTitle().equals("New Song"))
        .findFirst()
        .orElse(null);
    assertAll(
        () -> assertNotNull(createdSong),
        () -> assertThat(createdSong.getTitle(), is("New Song")),
        () -> assertThat(createdSong.getAlbum().getTitle(), is("Album1"))
    );
  }

  @Test
  public void testUpdate() {
    Long songId = testSongs.get(0).getId();
    SongData updatedSongData = new SongData("Updated Song", 5.0, Genre.POP, testAlbums.get(1).getId());

    songService.update(songId, updatedSongData);

    Song updatedSong = songService.getById(songId);
    assertAll(
        () -> assertThat(updatedSong, both(
            hasProperty("title", is("Updated Song"))
        ).and(
            hasProperty("duration", is(5.0))
        )),
        () -> assertThat(updatedSong.getAlbum().getTitle(), is("Album2"))
    );
  }

  @Test
  public void testUpdateThrowSongIsNotFoundException() {
    SongData songData = new SongData("Non-existent Song", 3.0, Genre.ROCK, 1L);
    Exception exception = assertThrows(InternalException.class, () -> songService.update(999L, songData));

    assertThat(exception.getMessage(), is(Exceptions.SONG_IS_NOT_FOUND.getMessage()));
  }

  @Test
  public void testUpdateThrowAlbumIsNotFoundException() {
    SongData songData = new SongData("Non-existent Song", 3.0, Genre.ROCK, 999L);
    Exception exception = assertThrows(InternalException.class, () -> songService.update(testSongs.get(0).getId(), songData));

    assertThat(exception.getMessage(), is(Exceptions.ALBUM_IS_NOT_FOUND.getMessage()));
  }

  @Test
  public void testDelete() {
    Long songIdToDelete = testSongs.get(0).getId();
    songService.delete(songIdToDelete);

    List<Song> songs = songService.getAll();
    assertThat(songs, hasSize(4));

    Optional<Song> deletedSong = songRepository.findById(songIdToDelete);
    assertSame(deletedSong, Optional.empty());
  }

  @Test
  public void testDeleteThrowSongIsNotFoundException() {
    Exception exception = assertThrows(InternalException.class, () -> songService.delete(999L));

    assertThat(exception.getMessage(), is(Exceptions.SONG_IS_NOT_FOUND.getMessage()));
  }

  @ParameterizedTest
  @MethodSource("provideGenresToCounts")
  public void testCountSongsByGenre(Genre genre, Long expectedCount) {
    long count = songService.countSongsByGenre(genre);
    assertEquals(expectedCount, count);
  }

  @ParameterizedTest
  @CsvSource({
      "ROCK, 2",
      "POP, 1",
      "JAZZ, 1",
      "CLASSICAL, 1"
  })
  public void testCsvCountSongsByGenre(Genre genre, Long expectedCount) {
    long count = songService.countSongsByGenre(genre);
    assertEquals(expectedCount, count);
  }

  public static Stream<Arguments> provideGenresToCounts() {
    return Stream.of(
        Arguments.of(Genre.ROCK, 2L),
        Arguments.of(Genre.POP, 1L),
        Arguments.of(Genre.JAZZ, 1L),
        Arguments.of(Genre.CLASSICAL, 1L)
    );
  }
}
