package com.barbosa.desafio_tech.domain.service;

import com.barbosa.desafio_tech.domain.dto.ComesticDTO;
import com.barbosa.desafio_tech.domain.dto.ComesticFilterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComesticService {

    private final FortniteApiService fortniteApiService;

    public Page<ComesticDTO> listAll(Pageable pageable, ComesticFilterDTO filter) {
        Pageable effectivePageable = normalizePageable(pageable);
        List<ComesticDTO> all = fortniteApiService.getAllCosmetics();
        List<ComesticDTO> filtered = applyFilter(all, filter);
        return paginate(filtered, effectivePageable);
    }

    public Page<ComesticDTO> listNew(Pageable pageable) {
        Pageable effectivePageable = normalizePageable(pageable);
        List<ComesticDTO> list = fortniteApiService.getNewCosmetics();
        return paginate(list, effectivePageable);
    }

    public Page<ComesticDTO> listShop(Pageable pageable) {
        Pageable effectivePageable = normalizePageable(pageable);
        List<ComesticDTO> list = fortniteApiService.getShopItems();
        return paginate(list, effectivePageable);
    }

    public ComesticDTO getById(String id) {
        return fortniteApiService.getCosmeticById(id);
    }

    private List<ComesticDTO> applyFilter(List<ComesticDTO> list, ComesticFilterDTO comestic) {
        if (list == null || list.isEmpty()) {
            return List.of();
        }
        if (comestic == null) return sortByName(list);
        return list.stream()
                .filter(c -> comestic.getName() == null || containsIgnoreCase(c.getName(), comestic.getName()))
                .filter(c -> comestic.getType() == null || Objects.equals(c.getType(), comestic.getType()))
                .filter(c -> comestic.getRarity() == null || Objects.equals(c.getRarity(), comestic.getRarity()))
                .filter(c -> comestic.getIsNew() == null || Objects.equals(c.getIsNew(), comestic.getIsNew()))
                .filter(c -> comestic.getIsOnSale() == null || Objects.equals(c.getIsOnSale(), comestic.getIsOnSale()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), this::sortByName));
    }

    private boolean containsIgnoreCase(String text, String token) {
        if (text == null || token == null) return false;
        return text.toLowerCase(Locale.ROOT).contains(token.toLowerCase(Locale.ROOT));
    }

    private Page<ComesticDTO> paginate(List<ComesticDTO> list, Pageable pageable) {
        if (list == null || list.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        if (start >= list.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, list.size());
        }
        List<ComesticDTO> content = list.subList(start, end);
        return new PageImpl<>(content, pageable, list.size());
    }

    private List<ComesticDTO> sortByName(List<ComesticDTO> cosmetics) {
        return cosmetics.stream()
                .sorted(Comparator.comparing(ComesticDTO::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toList());
    }

    private Pageable normalizePageable(Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            return PageRequest.of(0, 20);
        }
        if (pageable.getPageSize() <= 0) {
            return PageRequest.of(pageable.getPageNumber(), 20, pageable.getSort());
        }
        return pageable;
    }
}

