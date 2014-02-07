package com.rit.sucy.service;

public enum SuffixGroups {

    BLIND ("blinding"),
    BREATHING ("breathing"),
    DEFENSE ("defense"),
    DIGGING ("digging"),
    DURABILITY ("durability"),
    EXPLOSIONS ("explosions"),
    FALL ("fall"),
    FIRE ("fire"),
    FISHING ("fishing"),
    FORCE ("force"),
    HEALTH ("health"),
    INVISIBILITY ("invisibility"),
    JUMPING ("jumping"),
    LIFESTEAL ("life-steal"),
    LIGHTNING ("lightning"),
    LOOT ("loot"),
    NIGHT_VISION ("night-vision"),
    POISON ("poison"),
    PROJECTILE ("projectile"),
    SLOWING ("slowing"),
    SPEED ("speed"),
    STRENGTH ("strength"),
    TRAP ("trap"),
    WEAKNESS ("weakness"),
    WITHER ("wither"),
    ;

    private final String key;

    private SuffixGroups(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
