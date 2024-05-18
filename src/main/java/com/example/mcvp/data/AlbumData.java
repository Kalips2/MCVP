package com.example.mcvp.data;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AlbumData {
  @NotBlank
  private String title;
  @NotBlank
  private String releaseDate;
  @NotBlank
  private Long artistId;
  @NotBlank
  private Long labelId;
}
