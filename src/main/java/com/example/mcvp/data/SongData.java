package com.example.mcvp.data;

import com.example.mcvp.models.Genre;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SongData {
  @NotBlank
  private String title;
  @NotBlank
  private double duration;
  @NotBlank
  private Genre genre;
  @NotBlank
  private Long album_id;
}
