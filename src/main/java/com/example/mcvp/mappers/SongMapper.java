package com.example.mcvp.mappers;

import com.example.mcvp.data.SongData;
import com.example.mcvp.models.Album;
import com.example.mcvp.models.Song;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SongMapper {
  public Song dataToEntity(SongData song, Album album) {
    return Song.builder()
        .title(song.getTitle())
        .duration(song.getDuration())
        .genre(song.getGenre())
        .album(album)
        .build();
  }
}
