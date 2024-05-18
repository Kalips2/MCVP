package com.example.mcvp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {
  private Long id;
  private String title;
  private double duration;
  private Genre genre;
  private Album album;
}
