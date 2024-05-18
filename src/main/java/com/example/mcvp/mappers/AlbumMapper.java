package com.example.mcvp.mappers;

import com.example.mcvp.data.AlbumData;
import com.example.mcvp.models.Album;
import com.example.mcvp.models.Artist;
import com.example.mcvp.models.Label;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

@UtilityClass
public class AlbumMapper {

  public Album dataToEntity(AlbumData album, MultipartFile file, Artist artist, Label label) {
    return Album.builder()
        .title(album.getTitle())
        .releaseDate(DataMapper.dateFromString(album.getReleaseDate()))
        .artist(artist)
        .label(label)
        .pathToPhoto("Some random path to " + file.getName())
        .build();
  }
}
