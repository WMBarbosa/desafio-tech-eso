package com.barbosa.desafio_tech.domain.service;

import com.barbosa.desafio_tech.domain.dto.ComesticDTO;
import com.barbosa.desafio_tech.domain.response.FortniteCosmeticsResponse;
import com.barbosa.desafio_tech.domain.response.FortniteNewComesticResponse;
import com.barbosa.desafio_tech.domain.response.FortniteShopResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FortniteApiService {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(5);

    private final WebClient fortniteWebClient;

    public List<ComesticDTO> getAllCosmetics() {
        return fetchCosmetics("/cosmetics/br", false);
    }

    public List<ComesticDTO> getNewCosmetics() {
        return fetchCosmeticsNew("/cosmetics/new", false);
    }

    public List<ComesticDTO> getShopItems() {
        return fetchShop("/shop", true);
    }

    public ComesticDTO getCosmeticById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }

        return fortniteWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/cosmetics/br/{id}").build(id))
                .retrieve()
                .bodyToMono(FortniteSingleResponse.class)
                .timeout(DEFAULT_TIMEOUT)
                .map(FortniteSingleResponse::data)
                .map(this::mapCosmetic)
                .onErrorResume(ex -> {
                    log.warn("Falha ao consultar cosmetic {} na API do Fortnite", id, ex);
                    return Mono.empty();
                })
                .blockOptional()
                .orElse(null);
    }

    private List<ComesticDTO> fetchCosmeticsNew(String path, boolean markAsSale) {
        try {
            return fortniteWebClient.get()
                    .uri(path)
                    .retrieve()
                    .bodyToMono(FortniteNewComesticResponse.class)
                    .timeout(DEFAULT_TIMEOUT)
                    .map(response -> mapCosmetics(response.getData().getItems(), markAsSale))
                    .blockOptional()
                    .orElse(List.of());
        } catch (Exception ex) {
            log.warn("Falha ao consultar {} na API do Fortnite", path, ex);
            return List.of();
        }
    }

    private List<ComesticDTO> fetchCosmetics(String path, boolean markAsSale) {
        try {
            return fortniteWebClient.get()
                    .uri(path)
                    .retrieve()
                    .bodyToMono(FortniteCosmeticsResponse.class)
                    .timeout(DEFAULT_TIMEOUT)
                    .map(response -> {
                        if (response.getData() == null) {
                            return List.<ComesticDTO>of();
                        }
                        return mapCosmeticsAll(response.getData(), markAsSale);
                    })
                    .blockOptional()
                    .orElse(List.of());
        } catch (Exception ex) {
            log.warn("Falha ao consultar {} na API do Fortnite", path, ex);
            return List.of();
        }
    }



    private List<ComesticDTO> fetchShop(String path, boolean markAsSale) {
        try {
            return fortniteWebClient.get()
                    .uri(path)
                    .retrieve()
                    .bodyToMono(FortniteShopResponse.class)
                    .timeout(DEFAULT_TIMEOUT)
                    .map(response -> {
                        var entries = response.getData() != null ? response.getData().getEntries() : List.<FortniteShopResponse.StoreEntry>of();
                        return entries.stream()
                                .map(entry -> mapCosmeticShop(entry, markAsSale))
                                .filter(Objects::nonNull)
                                .toList();
                    })
                    .blockOptional()
                    .orElse(List.of());
        } catch (Exception ex) {
            log.warn("Falha ao consultar {} na API do Fortnite", path, ex);
            return List.of();
        }
    }

    private List<ComesticDTO> mapCosmeticsAll(List<Map<String, Object>> rawData, boolean markAsSale) {
        if (rawData == null || rawData.isEmpty()) {
            return List.of();
        }

        return rawData.stream()
                .map(this::mapCosmetic)
                .filter(Objects::nonNull)
                .map(dto -> markAsSale ? withSaleFlag(dto) : dto)
                .collect(Collectors.toList());
    }


    private ComesticDTO mapCosmeticShop(FortniteShopResponse.StoreEntry entry, boolean markAsSale) {
        if (entry == null) {
            return null;
        }

        return ComesticDTO.builder()
                .id(entry.getOfferId())
                .name(entry.getDevName())
                .price(entry.getFinalPrice())
                .isNew(entry.getInDate() != null && entry.getOutDate() == null)
                .isOnSale(markAsSale)
                .imageUrl(entry.getNewDisplayAssetPath())
                .build();
    }


    private List<ComesticDTO> mapCosmetics(Map<String, List<Map<String, Object>>> itemsMap, boolean markAsSale) {
        if (itemsMap == null || itemsMap.isEmpty()) {
            return List.of();
        }

        return itemsMap.values().stream()
                .flatMap(List::stream)
                .map(this::mapCosmetic)
                .filter(Objects::nonNull)
                .map(dto -> markAsSale ? withSaleFlag(dto) : dto)
                .collect(Collectors.toList());
    }



    private ComesticDTO mapCosmetic(Map<String, Object> item) {
        if (item == null || item.isEmpty()) {
            return null;
        }

        String id = Objects.toString(item.get("id"), null);
        String name = Objects.toString(item.get("name"), null);

        String type = extractValue(item.get("type"));
        String rarity = extractValue(item.get("rarity"));
        String imageUrl = extractImageUrl(item.get("images"));

        Integer price = extractPrice(item.get("price"), item.get("shopHistory"));
        Boolean isNew = extractBoolean(item.get("new"));
        Boolean isOnSale = item.containsKey("offerTag") && item.get("offerTag") != null ? Boolean.TRUE : extractBoolean(item.get("isOnSale"));

        return ComesticDTO.builder()
                .id(id)
                .name(name)
                .type(type)
                .rarity(rarity)
                .imageUrl(imageUrl)
                .price(price)
                .isNew(isNew)
                .isOnSale(isOnSale != null && isOnSale)
                .build();
    }

    private String extractValue(Object raw) {
        if (raw instanceof Map<?, ?> map) {
            Object value = map.get("value");
            if (value != null) {
                return Objects.toString(value, null);
            }
        }
        return raw != null ? raw.toString() : null;
    }

    private String extractImageUrl(Object imagesObj) {
        if (imagesObj instanceof Map<?, ?> imgMap) {
            Object icon = imgMap.get("icon");
            if (icon != null) {
                return icon.toString();
            }
            Object smallIcon = imgMap.get("smallIcon");
            if (smallIcon != null) {
                return smallIcon.toString();
            }
        }
        return null;
    }

    private Integer extractPrice(Object priceObj, Object shopHistory) {
        if (priceObj instanceof Number number) {
            return number.intValue();
        }

        if (priceObj instanceof Map<?, ?> priceMap) {
            Object finalPrice = priceMap.get("finalPrice");
            if (finalPrice instanceof Number number) {
                return number.intValue();
            }
            Object regularPrice = priceMap.get("regularPrice");
            if (regularPrice instanceof Number number) {
                return number.intValue();
            }
        }

        if (shopHistory instanceof List<?> history && !history.isEmpty()) {
            Object last = history.get(history.size() - 1);
            if (last instanceof Number number) {
                return number.intValue();
            }
        }

        return null;
    }

    private Boolean extractBoolean(Object raw) {
        if (raw instanceof Boolean bool) {
            return bool;
        }
        if (raw == null) {
            return null;
        }
        if ("true".equalsIgnoreCase(raw.toString())) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(raw.toString())) {
            return Boolean.FALSE;
        }
        return null;
    }

    private ComesticDTO withSaleFlag(ComesticDTO dto) {
        return ComesticDTO.builder()
                .id(dto.getId())
                .name(dto.getName())
                .type(dto.getType())
                .rarity(dto.getRarity())
                .imageUrl(dto.getImageUrl())
                .price(dto.getPrice())
                .isNew(dto.getIsNew())
                .isOnSale(Boolean.TRUE)
                .build();
    }

    private record FortniteSingleResponse(Map<String, Object> data) {
    }
}
