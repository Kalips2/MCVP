package com.example.mcvp.mappers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DataMapper {
  private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  public Date dateFromString(String input) {
    try {
      return new Date(DATE_FORMAT.parse(input).getTime());
    } catch (ParseException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public String stringFromDate(Date input) {
    return DATE_FORMAT.format(input);
  }

}
