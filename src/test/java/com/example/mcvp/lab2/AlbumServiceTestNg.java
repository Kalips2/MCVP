package com.example.mcvp.lab2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.example.mcvp.data.AlbumData;
import com.example.mcvp.exceptions.Exceptions;
import com.example.mcvp.exceptions.InternalException;
import com.example.mcvp.mappers.DataMapper;
import com.example.mcvp.models.Album;
import com.example.mcvp.models.Artist;
import com.example.mcvp.models.CoordinateDto;
import com.example.mcvp.models.Label;
import com.example.mcvp.repositories.impl.AlbumRepositoryImpl;
import com.example.mcvp.repositories.impl.ArtistRepositoryImpl;
import com.example.mcvp.repositories.impl.LabelRepositoryImpl;
import com.example.mcvp.services.impl.AlbumServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockMultipartFile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlbumServiceTestNg {
  static AlbumRepositoryImpl albumRepository;
  static LabelRepositoryImpl labelRepository;
  static ArtistRepositoryImpl artistRepository;

  static List<Album> testAlbums;
  static List<Artist> testArtists;
  static List<Label> testLabels;
  static MockMultipartFile file;

  private static AlbumServiceImpl albumService;

  @BeforeSuite(groups = {"get", "delete", "count", "create-update"})
  public static void setupAll() {
    file = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());

    albumRepository = new AlbumRepositoryImpl();
    labelRepository = new LabelRepositoryImpl();
    artistRepository = new ArtistRepositoryImpl();

    albumService = new AlbumServiceImpl(albumRepository, artistRepository, labelRepository);
  }

  @BeforeMethod(groups = {"get", "delete", "count", "create-update"})
  public void setupTest() {
    resetElements();

    List<Artist> savedArtists = StreamSupport.stream(artistRepository.saveAll( testArtists).spliterator(), false).toList();
    List<Label> savedLabels = StreamSupport.stream(labelRepository.saveAll(testLabels).spliterator(), false).toList();

    for (int i = 0; i < testAlbums.size(); i++) {
      Album album = testAlbums.get(i);
      album.setArtist(savedArtists.get(i % savedArtists.size()));
      album.setLabel(savedLabels.get(i % savedLabels.size()));
      albumRepository.save(album);
    }
  }

  private void resetElements() {
    albumRepository.deleteAll();
    artistRepository.deleteAll();
    labelRepository.deleteAll();

    testArtists = Arrays.asList(
        new Artist(null, "John", "Doe", DataMapper.dateFromString("1995-10-10")),
        new Artist(null, "Jane", "Smith", DataMapper.dateFromString("1995-9-10")),
        new Artist(null, "Kim", "Kardashian", DataMapper.dateFromString("1995-8-10"))
    );

    testLabels = Arrays.asList(
        new Label(null, "Label 1",new CoordinateDto("40.0, 40.0")),
        new Label(null, "Label 2",new CoordinateDto("50.0, 50.0"))
    );

    testAlbums = Arrays.asList(
        new Album(null, "Album1", DataMapper.dateFromString("2023-10-10"), "path/to/photo/1", null, null),
        new Album(null, "Album2", DataMapper.dateFromString("2022-10-10"), "path/to/photo/2", null, null),
        new Album(null, "Album3", DataMapper.dateFromString("2021-10-10"), "path/to/photo/3", null, null),
        new Album(null, "Album4", DataMapper.dateFromString("2022-9-10"), "path/to/photo/4", null, null),
        new Album(null, "Album5", DataMapper.dateFromString("2022-11-10"), "path/to/photo/5", null, null),
        new Album(null, "Album6", DataMapper.dateFromString("2022-12-10"), "path/to/photo/6", null, null)
    );

  }

  @Test(groups = "get")
  public void testGetAll() {
    List<Album> result = albumService.getAll();

    assumeTrue(!result.isEmpty());

    assertAll(
        () -> assertThat(result, containsInAnyOrder(
            hasProperty("title", is("Album1")),
            hasProperty("title", is("Album2")),
            hasProperty("title", is("Album3")),
            hasProperty("title", is("Album4")),
            hasProperty("title", is("Album5")),
            hasProperty("title", is("Album6"))
        )),
        () -> assertThat(result.stream().map(Album::getArtist).collect(Collectors.toList()), hasItems(
            hasProperty("name", is("John")),
            hasProperty("name", is("Jane"))
        )),
        () -> {
          List<String> paths = result.stream().map(Album::getPathToPhoto).collect(Collectors.toList());
          assertThat(paths, everyItem(containsString("path/to/photo")));
        }
    );
  }

  @Test(groups = "get")
  public void testGetById() {
    Album album = albumService.getById(testAlbums.get(0).getId());

    assertAll(
        () -> assertNotNull(album),
        () -> assertThat(album.getTitle(), is("Album1")),
        () -> assertThat(album.getArtist().getName(), is("John")),
        () -> assertThat(album.getLabel().getName(), is("Label 1"))
    );
  }

  @Test(groups = "get")
  public void testGetByIdThrowException() {
    Exception exception = assertThrows(InternalException.class, () -> albumService.getById(-1L));

    assertThat(exception.getMessage(), is(Exceptions.ALBUM_IS_NOT_FOUND.getMessage()));
  }

  @Test(groups = "create-update")
  public void testCreate() {
    AlbumData newAlbumData = new AlbumData("New Album", "1900-01-01", testArtists.get(0).getId(), testLabels.get(0).getId());
    albumService.create(newAlbumData, file);

    List<Album> result = albumService.getAll();
    assertThat(result, hasSize(7));

    Album createdAlbum = result.stream()
        .filter(a -> a.getReleaseDate().equals(DataMapper.dateFromString("1900-01-01")))
        .findFirst()
        .orElse(null);
    assertAll(
        () -> assertNotNull(createdAlbum),
        () -> assertThat(createdAlbum.getTitle(), is("New Album")),
        () -> assertThat(createdAlbum.getArtist().getName(), is("John")),
        () -> assertThat(createdAlbum.getLabel().getName(), is("Label 1"))
    );
  }

  @Test(groups = "create-update")
  public void testUpdate() {
    Long albumId = testAlbums.get(0).getId();
    AlbumData updatedAlbumData = new AlbumData("New Title", "2022-01-01", testArtists.get(1).getId(), testLabels.get(1).getId());

    albumService.update(albumId, updatedAlbumData, file);

    Album updatedAlbum = albumService.getById(albumId);
    assertAll(
        () -> assertThat(updatedAlbum, both(
            hasProperty("title", is("New Title"))
        ).and(
            hasProperty("pathToPhoto", containsString("Updated photo path"))
        )),
        () -> assertThat(updatedAlbum.getReleaseDate(), is(notNullValue())),
        () -> assertThat(updatedAlbum.getArtist().getName(), startsWith("Jane")),
        () -> assertThat(updatedAlbum.getLabel().getName(), endsWith("Label 2"))
    );
  }

  @Test(groups = "create-update")
  public void testUpdateThrowAlbumIsNotFoundException() {
    AlbumData albumData = new AlbumData("Non-existent Album", "2022-01-01", testArtists.get(0).getId(), testLabels.get(0).getId());
    Exception exception = assertThrows(InternalException.class, () -> albumService.update(-1L, albumData, file));

    assertThat(exception.getMessage(), is(Exceptions.ALBUM_IS_NOT_FOUND.getMessage()));
  }

  @Test(groups = "create-update")
  public void testUpdateThrowLabelIsNotFoundException() {
    AlbumData albumData = new AlbumData("Non-existent Album", "2022-01-01", testArtists.get(0).getId(), -1L);
    Exception exception = assertThrows(InternalException.class, () -> albumService.update(testAlbums.get(0).getId(), albumData, file));

    assertThat(exception.getMessage(), is(Exceptions.LABEL_IS_NOT_FOUND.getMessage()));
  }

  @Test(groups = "create-update")
  public void testUpdateThrowArtistIsNotFoundException() {
    AlbumData albumData = new AlbumData("Non-existent Album", "2022-01-01", -1L, testLabels.get(0).getId());
    Exception exception = assertThrows(InternalException.class, () -> albumService.update(testAlbums.get(0).getId(), albumData, file));

    assertThat(exception.getMessage(), is(Exceptions.ARTIST_IS_NOT_FOUND.getMessage()));
  }

  @Test(groups = "delete")
  public void testDelete() {
    Long albumIdToDelete = testAlbums.get(0).getId();
    albumService.delete(albumIdToDelete);

    List<Album> albums = albumService.getAll();
    assertThat(albums, hasSize(5));

    Optional<Album> deletedAlbum = albumRepository.findById(albumIdToDelete);
    assertSame(deletedAlbum, Optional.empty());
  }

  @Test(groups = "delete")
  public void testDeleteThrowAlbumsIsNotFoundException() {
    Exception exception = assertThrows(InternalException.class, () -> albumService.delete(-1L));

    assertThat(exception.getMessage(), is(Exceptions.ALBUM_IS_NOT_FOUND.getMessage()));
  }

  @ParameterizedTest
  @MethodSource("provideLabelNameToItsCount")
  public void testCountAlbumsByLabelId(String labelName, Long expectedCountOfLabels) {
    assertEquals(expectedCountOfLabels, albumService.countAlbumsByLabelName(labelName));
  }

  @Test(groups = "count", dataProvider = "provideLabelNameToItsCount")
  public void testCsvCountAlbumsByLabelId(String labelName, Long expectedCountOfLabels) {
    assertEquals(expectedCountOfLabels, albumService.countAlbumsByLabelName(labelName));
  }

  @DataProvider(name = "provideLabelNameToItsCount")
  public static Object[][] provideLabelNameToItsCount() {
    return new Object[][]{
      {"Label 1", 3L},
      {"Label 2", 3L}
    };
  }
}
