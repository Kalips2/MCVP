package com.example.mcvp.controllers;

import com.example.mcvp.data.SongData;
import com.example.mcvp.models.Song;
import com.example.mcvp.services.SongService;
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
@RequestMapping("/rest/songs")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SongRestController {
  SongService songService;

  @GetMapping("")
  public ResponseEntity<List<Song>> getAllSongs() {
    List<Song> songs = songService.getAll();
    return ResponseEntity.ok(songs);
  }

  @PostMapping("/create")
  public ResponseEntity<?> createSong(@RequestPart("song") SongData songData) {
    try {
      songService.create(songData);
      return ResponseEntity.status(HttpStatus.OK).build();
    } catch (RuntimeException e) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }

  @PostMapping("/update")
  public ResponseEntity<?> updateSong(@RequestPart("id") Long id,
                                      @RequestPart("song") SongData songData) {
    try {
      songService.update(id, songData);
      return ResponseEntity.ok().build();
    } catch (RuntimeException e) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }

  @PostMapping("/delete")
  public ResponseEntity<?> deleteSong(@RequestPart Long id) {
    try {
      songService.delete(id);
      return ResponseEntity.ok().build();
    } catch (RuntimeException e) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }
}
