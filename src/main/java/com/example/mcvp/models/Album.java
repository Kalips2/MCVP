package com.example.mcvp.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Album {
  private Long id;
  private String title;
  private Date releaseDate;
  private String pathToPhoto;
  private Artist artist;
  private Label label;
}
