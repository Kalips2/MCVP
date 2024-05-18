package com.example.mcvp.controllers;

import com.example.mcvp.data.AlbumData;
import com.example.mcvp.models.Album;
import com.example.mcvp.services.AlbumService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/rest/albums")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AlbumRestController {
  AlbumService albumService;

  @GetMapping("")
  public ResponseEntity<List<Album>> getAllAlbums() {
    List<Album> albums = albumService.getAll();
    return ResponseEntity.ok(albums);
  }

  @PostMapping("/create")
  public ResponseEntity<?> createAlbum(@RequestPart("album") AlbumData albumData,
                                       @RequestPart("file") MultipartFile file) {
    try {
      albumService.create(albumData, file);
      return ResponseEntity.status(HttpStatus.OK).build();
    } catch (RuntimeException e) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }

  @PostMapping("/update")
  public ResponseEntity<?> updateAlbum(@RequestParam("id") Long id,
                                       @RequestPart("album") AlbumData album,
                                       @RequestPart("file") MultipartFile file) {
    try {
      albumService.update(id, album, file);
      return ResponseEntity.ok().build();
    } catch (RuntimeException e) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }

  @PostMapping("/delete")
  public ResponseEntity<?> deleteAlbum(@RequestPart Long id) {
    try {
      albumService.delete(id);
      return ResponseEntity.ok().build();
    } catch (RuntimeException e) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }
}
