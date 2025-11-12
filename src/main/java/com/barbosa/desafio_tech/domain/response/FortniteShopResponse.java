package com.barbosa.desafio_tech.domain.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FortniteShopResponse {

    private int status;
    private StoreData data;

    @Data
    public static class StoreData {
        private String hash;
        private String date;
        private String vbuckIcon;
        private List<StoreEntry> entries;
    }

    @Data
    public static class StoreEntry {
        private int regularPrice;
        private int finalPrice;
        private String devName;
        private String offerId;
        private String inDate;
        private String outDate;
        private OfferTag offerTag;
        private boolean giftable;
        private boolean refundable;
        private int sortPriority;
        private String layoutId;
        private Layout layout;
        private String tileSize;
        private String newDisplayAssetPath;
        private List<Track> tracks;
    }

    @Data
    public static class OfferTag {
        private String id;
        private String text;
    }

    @Data
    public static class Layout {
        private String id;
        private String name;
        private int index;
        private int rank;
        private String showIneligibleOffers;
        private boolean useWidePreview;
        private String displayType;
    }

    @Data
    public static class Track {
        private String id;
        private String devName;
        private String title;
        private String artist;
        private int releaseYear;
        private int bpm;
        private int duration;
        private Difficulty difficulty;
        private String albumArt;
        private String added;
    }

    @Data
    public static class Difficulty {
        private int vocals;
        private int guitar;
        private int bass;
        private int plasticBass;
        private int drums;
        private int plasticDrums;
    }
}




