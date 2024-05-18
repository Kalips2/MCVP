package com.example.mcvp.data;

import com.example.mcvp.models.CoordinateDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LabelData {
  @NotBlank
  private String name;
  private CoordinateDto coordinates;

  public LabelData(String name, String coordinates) {
    this.name = name;
    this.coordinates = new CoordinateDto(coordinates);
  }
}
