package com.barbosa.desafio_tech.controller;

import com.barbosa.desafio_tech.domain.dto.ComesticDTO;
import com.barbosa.desafio_tech.domain.dto.ComesticFilterDTO;
import com.barbosa.desafio_tech.domain.service.ComesticService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cosmetics")
@RequiredArgsConstructor
public class ComesticController {

    private final ComesticService comesticService;

    @GetMapping
    public ResponseEntity<Page<ComesticDTO>> allCosmetics(Pageable pageable, ComesticFilterDTO filterDTO) {
        Page<ComesticDTO> cosmetics = comesticService.listAll(pageable, filterDTO);
        return ResponseEntity.ok(cosmetics);
    }

    @GetMapping("/new")
    public ResponseEntity<Page<ComesticDTO>> newCosmetics(Pageable pageable) {
        Page<ComesticDTO> cosmetics = comesticService.listNew(pageable);
        return ResponseEntity.ok(cosmetics);
    }

    @GetMapping("/shop")
    public ResponseEntity<Page<ComesticDTO>> shopCosmetics(Pageable pageable) {
        Page<ComesticDTO> cosmetics = comesticService.listShop(pageable);
        return ResponseEntity.ok(cosmetics);
    }

}
