package com.example.mcvp.data;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArtistData {
  @NotBlank
  private String name;
  @NotBlank
  private String surname;
  @NotBlank
  private String dateOfBirth;
}
