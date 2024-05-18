package com.example.mcvp.controllers;

import com.example.mcvp.data.ArtistData;
import com.example.mcvp.models.Artist;
import com.example.mcvp.services.ArtistService;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/rest/artists")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ArtistRestController {
  ArtistService artistService;

  @GetMapping("")
  public ResponseEntity<List<Artist>> getAllArtists() {
    List<Artist> artists = artistService.getAll();
    return ResponseEntity.ok(artists);
  }

  @PostMapping("/create")
  public ResponseEntity<?> createArtist(@RequestPart("artist") ArtistData artistData) {
    try {
      artistService.create(artistData);
      return ResponseEntity.status(HttpStatus.OK).build();
    } catch (RuntimeException e) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }

  @PostMapping("/update")
  public ResponseEntity<?> updateArtist(@RequestPart("id") Long id,
                                        @RequestPart("artist") ArtistData artistData) {
    try {
      artistService.update(id, artistData);
      return ResponseEntity.status(HttpStatus.OK).build();
    } catch (RuntimeException e) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }

  @PostMapping("/delete")
  public ResponseEntity<?> deleteArtist(@RequestPart Long id) {
    try {
      artistService.delete(id);
      return ResponseEntity.status(HttpStatus.OK).build();
    } catch (RuntimeException e) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }
}
