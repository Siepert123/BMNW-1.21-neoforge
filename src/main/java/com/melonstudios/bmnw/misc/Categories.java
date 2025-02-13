package com.melonstudios.bmnw.misc;

import com.melonstudios.bmnw.category.BombCategory;
import com.melonstudios.bmnw.category.MissileCategory;

public class Categories {
    private Categories() {}

    public static final BombCategory BRICK_BOMB = BombCategory.of("brick", 0xff8888);
    public static final BombCategory NUCLEAR_BOMB = BombCategory.of("nuclear", 0x00ff00);
    public static final BombCategory THERMONUCLEAR_BOMB = BombCategory.of("thermonuclear", 0x00ff00);
    public static final BombCategory SOULFIRE_BOMB = BombCategory.of("soulfire", 0x8888ff);
    public static final BombCategory FRACTURIZER_BOMB = BombCategory.of("fracturizer");

    public static final MissileCategory BRICK_MISSILE = MissileCategory.of("brick", 0xff8888);
    public static final MissileCategory HE_MISSILE = MissileCategory.of("he");
    public static final MissileCategory NUCLEAR_MISSILE = MissileCategory.of("nuclear", 0x00ff00);
    public static final MissileCategory THERMONUCLEAR_MISSILE = MissileCategory.of("thermonuclear", 0x00ff00);
    public static final MissileCategory SOULFIRE_MISSILE = MissileCategory.of("soulfire", 0x8888ff);
}
