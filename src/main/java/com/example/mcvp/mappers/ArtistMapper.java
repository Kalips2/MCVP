package com.example.mcvp.mappers;

import com.example.mcvp.data.ArtistData;
import com.example.mcvp.models.Artist;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ArtistMapper {

  public Artist dataToEntity(ArtistData artist) {
    return Artist.builder()
        .name(artist.getName())
        .surname(artist.getSurname())
        .dateOfBirth(DataMapper.dateFromString(artist.getDateOfBirth()))
        .build();
  }
}
