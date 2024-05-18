package com.example.mcvp.lab2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.example.mcvp.data.ArtistData;
import com.example.mcvp.exceptions.Exceptions;
import com.example.mcvp.exceptions.InternalException;
import com.example.mcvp.mappers.DataMapper;
import com.example.mcvp.models.Artist;
import com.example.mcvp.repositories.impl.ArtistRepositoryImpl;
import com.example.mcvp.services.impl.ArtistServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArtistServiceTestNg {
  static ArtistRepositoryImpl artistRepository;

  static List<Artist> testArtists;

  static ArtistServiceImpl artistService;

  @BeforeSuite(groups = {"get", "delete", "count", "create-update"})
  public static void setupAll() {
    artistRepository = new ArtistRepositoryImpl();
    artistService = new ArtistServiceImpl(artistRepository);
  }

  @BeforeMethod(groups = {"get", "delete", "count", "create-update"})
  public void setupTest() {
    resetElements();

    artistRepository.saveAll(testArtists);
  }

  private void resetElements() {
    artistRepository.deleteAll();

    testArtists = Arrays.asList(
        new Artist(null, "John", "Doe", DataMapper.dateFromString("1995-10-10")),
        new Artist(null, "Jane", "Smith", DataMapper.dateFromString("1995-9-10")),
        new Artist(null, "Kim", "Kardashian", DataMapper.dateFromString("1995-8-10")),
        new Artist(null, "Michael", "Jordan", DataMapper.dateFromString("1995-7-10")),
        new Artist(null, "Elon", "Musk", DataMapper.dateFromString("1995-6-10")),
        new Artist(null, "Bill", "Gates", DataMapper.dateFromString("1995-5-10"))
    );
  }

  @Test(groups = "get")
  public void testGetAll() {
    List<Artist> result = artistService.getAll();

    assumeTrue(!result.isEmpty());

    assertAll(
        () -> assertThat(result, hasSize(6)),
        () -> assertThat(result, containsInAnyOrder(
            hasProperty("name", is("John")),
            hasProperty("name", is("Jane")),
            hasProperty("name", is("Kim")),
            hasProperty("name", is("Michael")),
            hasProperty("name", is("Elon")),
            hasProperty("name", is("Bill"))
        )),
        () -> assertThat(result.stream().map(Artist::getSurname).collect(Collectors.toList()), hasItems(
            is("Doe"),
            is("Smith")
        ))
    );
  }

  @Test(groups = "get")
  public void testGetById() {
    Artist artist = artistService.getById(testArtists.get(0).getId());

    assertAll(
        () -> assertNotNull(artist),
        () -> assertThat(artist.getName(), is("John")),
        () -> assertThat(artist.getSurname(), is("Doe")),
        () -> assertThat(artist.getDateOfBirth(), is(DataMapper.dateFromString("1995-10-10")))
    );
  }

  @Test(groups = "get")
  public void testGetByIdThrowException() {
    Exception exception = assertThrows(InternalException.class, () -> artistService.getById(-1L));

    assertThat(exception.getMessage(), is(Exceptions.ARTIST_IS_NOT_FOUND.getMessage()));
  }

  @Test(groups = "create-update")
  public void testCreate() {
    ArtistData newArtistData = new ArtistData("New", "Artist", "2000-01-01");
    artistService.create(newArtistData);

    List<Artist> result = artistService.getAll();
    assertThat(result, hasSize(7));

    Artist createdArtist = result.stream()
        .filter(a -> a.getName().equals("New"))
        .findFirst()
        .orElse(null);
    assertAll(
        () -> assertNotNull(createdArtist),
        () -> assertThat(createdArtist.getName(), is("New")),
        () -> assertThat(createdArtist.getSurname(), is("Artist")),
        () -> assertThat(createdArtist.getDateOfBirth(), is(DataMapper.dateFromString("2000-01-01")))
    );
  }

  @Test(groups = "create-update")
  public void testUpdate() {
    Long artistId = testArtists.get(0).getId();
    ArtistData updatedArtistData = new ArtistData("Updated", "Artist", "2001-01-01");

    artistService.update(artistId, updatedArtistData);

    Artist updatedArtist = artistService.getById(artistId);
    assertAll(
        () -> assertThat(updatedArtist, both(
            hasProperty("name", is("Updated"))
        ).and(
            hasProperty("surname", is("Artist"))
        )),
        () -> assertThat(updatedArtist.getDateOfBirth(), is(DataMapper.dateFromString("2001-01-01")))
    );
  }

  @Test(groups = "create-update")
  public void testUpdateThrowArtistIsNotFoundException() {
    ArtistData artistData = new ArtistData("Non-existent", "Artist", "2002-01-01");
    Exception exception = assertThrows(InternalException.class, () -> artistService.update(-1L, artistData));

    assertThat(exception.getMessage(), is(Exceptions.ARTIST_IS_NOT_FOUND.getMessage()));
  }

  @Test(groups = "delete")
  public void testDelete() {
    Long artistIdToDelete = testArtists.get(0).getId();
    artistService.delete(artistIdToDelete);

    List<Artist> artists = artistService.getAll();
    assertThat(artists, hasSize(5));

    Optional<Artist> deletedArtist = artistRepository.findById(artistIdToDelete);
    assertSame(deletedArtist, Optional.empty());
  }

  @Test(groups = "delete")
  public void testDeleteThrowArtistIsNotFoundException() {
    Exception exception = assertThrows(InternalException.class, () -> artistService.delete(-1L));

    assertThat(exception.getMessage(), is(Exceptions.ARTIST_IS_NOT_FOUND.getMessage()));
  }
}

