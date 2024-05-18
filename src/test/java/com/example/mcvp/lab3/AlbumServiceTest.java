package com.example.mcvp.lab3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.mcvp.data.AlbumData;
import com.example.mcvp.exceptions.Exceptions;
import com.example.mcvp.exceptions.InternalException;
import com.example.mcvp.mappers.DataMapper;
import com.example.mcvp.models.Album;
import com.example.mcvp.models.Artist;
import com.example.mcvp.models.CoordinateDto;
import com.example.mcvp.models.Label;
import com.example.mcvp.repositories.AlbumRepository;
import com.example.mcvp.repositories.ArtistRepository;
import com.example.mcvp.repositories.LabelRepository;
import com.example.mcvp.services.impl.AlbumServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class AlbumServiceTest {
  @Mock
  private AlbumRepository albumRepository;

  @Mock
  private ArtistRepository artistRepository;

  @Mock
  private LabelRepository labelRepository;

  @Spy
  @InjectMocks
  private AlbumServiceImpl albumService;

  static List<Album> testAlbums;
  static List<Artist> testArtists;
  static List<Label> testLabels;
  private MultipartFile testFile;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    testArtists = Arrays.asList(
        new Artist(1L, "John", "Doe", DataMapper.dateFromString("1995-10-10")),
        new Artist(2L, "Jane", "Smith", DataMapper.dateFromString("1995-9-10")),
        new Artist(3L, "Kim", "Kardashian", DataMapper.dateFromString("1995-8-10"))
    );

    testLabels = Arrays.asList(
        new Label(1L, "Label 1",new CoordinateDto("40.0, 40.0")),
        new Label(2L, "Label 2",new CoordinateDto("50.0, 50.0"))
    );

    testAlbums = Arrays.asList(
        new Album(1L, "Album1", DataMapper.dateFromString("2023-10-10"), "path/to/photo/1", testArtists.get(0), testLabels.get(0)),
        new Album(2L, "Album2", DataMapper.dateFromString("2022-10-10"), "path/to/photo/2", testArtists.get(1), testLabels.get(1)),
        new Album(3L, "Album3", DataMapper.dateFromString("2021-10-10"), "path/to/photo/3", testArtists.get(2), testLabels.get(1)),
        new Album(4L, "Album4", DataMapper.dateFromString("2022-9-10"), "path/to/photo/4", testArtists.get(2), testLabels.get(1)),
        new Album(5L, "Album5", DataMapper.dateFromString("2022-11-10"), "path/to/photo/5", testArtists.get(1), testLabels.get(1)),
        new Album(6L, "Album6", DataMapper.dateFromString("2022-12-10"), "path/to/photo/6", testArtists.get(2), testLabels.get(0))
    );

    testFile = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());
  }

  @Test
  public void testGetAll() {
    when(albumRepository.findAll()).thenReturn(testAlbums);

    List<Album> result = albumService.getAll();

    assertEquals(6, result.size());

    verify(albumRepository, times(1)).findAll();
  }

  @Test
  public void testGetById() {
    when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbums.get(0)));

    Album album = albumService.getById(1L);

    assertNotNull(album);
    assertEquals("Album1", album.getTitle());
    assertEquals(DataMapper.dateFromString("2023-10-10"), album.getReleaseDate());
    assertEquals("path/to/photo/1", album.getPathToPhoto());
    assertEquals(1, album.getArtist().getId());
    assertEquals(1, album.getLabel().getId());

    verify(albumRepository, times(1)).findById(1L);
  }

  @Test
  public void testGetByIdNotFound() {
    when(albumRepository.findById(1L)).thenReturn(Optional.empty());

    Exception exception = assertThrows(InternalException.class, () -> albumService.getById(1L));

    assertEquals(Exceptions.ALBUM_IS_NOT_FOUND.getMessage(), exception.getMessage());
    verify(albumRepository, times(1)).findById(1L);
  }

  @Test
  public void testCreate() {
    when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtists.get(0)));
    when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabels.get(0)));
    when(albumRepository.save(any(Album.class))).thenReturn(testAlbums.get(0));

    AlbumData albumData = new AlbumData("Test Album", "2022-01-01", 1L, 1L);
    albumService.create(albumData, testFile);

    verify(artistRepository, times(1)).findById(1L);
    verify(labelRepository, times(1)).findById(1L);
    verify(albumRepository, times(1)).save(any(Album.class));
  }

  @Test
  public void testCreateArtistNotFound() {
    when(artistRepository.findById(1L)).thenReturn(Optional.empty());

    AlbumData albumData = new AlbumData("Test Album", "2022-01-01", 1L, 1L);
    Exception exception = assertThrows(InternalException.class, () -> albumService.create(albumData, testFile));

    assertEquals(Exceptions.ARTIST_IS_NOT_FOUND.getMessage(), exception.getMessage());
    verify(artistRepository, times(1)).findById(1L);
    verify(labelRepository, never()).findById(anyLong());
    verify(albumRepository, never()).save(any(Album.class));
  }

  @Test
  public void testUpdate() {
    when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbums.get(0)));
    when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtists.get(0)));
    when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabels.get(0)));
    AlbumData albumData = new AlbumData("Updated Album", "2022-01-01", 1L, 1L);

    albumService.update(1L, albumData, testFile);

    verify(albumRepository, times(1)).findById(1L);
    verify(artistRepository, times(1)).findById(1L);
    verify(labelRepository, times(1)).findById(1L);
    verify(albumRepository, times(1)).save(any(Album.class));
  }

  @Test
  public void testUpdateAlbumNotFound() {
    when(albumRepository.findById(1L)).thenReturn(Optional.empty());
    AlbumData albumData = new AlbumData("Updated Album", "2022-01-01", 1L, 1L);

    Exception exception = assertThrows(InternalException.class, () -> albumService.update(1L, albumData, testFile));
    assertEquals(Exceptions.ALBUM_IS_NOT_FOUND.getMessage(), exception.getMessage());

    verify(albumRepository, times(1)).findById(1L);
    verify(artistRepository, never()).findById(anyLong());
    verify(labelRepository, never()).findById(anyLong());
    verify(albumRepository, never()).save(any(Album.class));
  }

  @Test
  public void testDelete() {
    when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbums.get(0)));

    albumService.delete(1L);

    verify(albumRepository, times(1)).findById(1L);
    verify(albumRepository, times(1)).delete(any(Album.class));
  }

  @Test
  public void testDeleteAlbumNotFound() {
    when(albumRepository.findById(1L)).thenReturn(Optional.empty());

    Exception exception = assertThrows(InternalException.class, () -> albumService.delete(1L));
    assertEquals(Exceptions.ALBUM_IS_NOT_FOUND.getMessage(), exception.getMessage());

    verify(albumRepository, times(1)).findById(1L);
    verify(albumRepository, never()).delete(any(Album.class));
  }
}
