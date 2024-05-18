package com.example.mcvp.controllers;

import com.example.mcvp.data.LabelData;
import com.example.mcvp.models.Label;
import com.example.mcvp.services.LabelService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/rest/labels")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LabelRestController {
  LabelService labelService;

  @GetMapping("")
  public ResponseEntity<List<Label>> getAllLabels() {
    List<Label> labels = labelService.getAll();
    return ResponseEntity.ok(labels);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Label> getLabelById(@PathVariable Long id) {
    Label label = labelService.getById(id);
    return ResponseEntity.ok(label);
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<Label> getLabelByName(@PathVariable String name) {
    Label label = labelService.getByName(name).get();
    return ResponseEntity.ok(label);
  }

  @GetMapping("/findByName/{name}")
  public @ResponseBody List<String> getLabelsByName(@PathVariable String name) {
    return labelService.getNamesThatContain(name);
  }

  @PostMapping("/create")
  public ResponseEntity<?> createLabel(@RequestParam String name,
                                       @RequestParam String coordinates) {
    try {
      LabelData labelData = new LabelData(name, coordinates);
      Long id = labelService.create(labelData);
      return ResponseEntity.status(HttpStatus.OK).body(id);
    } catch (RuntimeException e) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }

  @PutMapping("/open/update")
  public ResponseEntity<?> updateLabel(@RequestParam Long id,
                                       @RequestParam String name,
                                       @RequestParam String coordinates) {
    try {
      LabelData labelData = new LabelData(name, coordinates);
      labelService.update(id, labelData);
      return ResponseEntity.ok().build();
    } catch (RuntimeException e) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }

  @DeleteMapping("/delete")
  public ResponseEntity<?> deleteLabel(@RequestParam Long id) {
    try {
      labelService.delete(id);
      return ResponseEntity.ok().build();
    } catch (RuntimeException e) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }

}
