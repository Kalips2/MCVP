package com.example.mcvp.mappers;

import com.example.mcvp.data.LabelData;
import com.example.mcvp.models.Label;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LabelMapper {
  public Label dataToEntity(LabelData label) {
    return Label.builder()
        .name(label.getName())
        .coordinates(label.getCoordinates())
        .build();
  }
}
