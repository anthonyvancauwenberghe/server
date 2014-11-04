package org.hyperion.rs2.model.color;

/**
 * Created by Jet on 10/8/2014.
 */
public enum Color {

    BLACK(898),
    WHITE(127),
    BLUE(359770),
    GREEN(419770),
    BRONZE(266770),
    PURPLE(374770),
    AQUA(226770),
    CYAN(34770),
    RED(933),
    YELLOW(8128),
    LIME(17350),
    DRAGON(926),
    LAVA(6073),
    ICING(491770),
    ORANGE(332770),
    DARK_GREEN(356770),
    DARK_BLUE(302770),
    DARK_CYAN(296770),
    PINK_RED(130770),
    GUM_PINK(129770),
    PINK(123770),
    CRAYON_BLUE(305770),
    IRON(33),
    STEEL(61),
    SILVER(43096),
    GOLD(8128),
    MITHRIL(43297),
    ADAMANT(21662),
    RUNE(36133),
    ZAMORAK(937),
    GUTHIX(21939),
    DRAGONSTONE(51111),
    BARROWS(10512),
    VOID(4550),
    VOID2(43528),
    PARTY_YELLOW(11200),
    PARTY_BLUE(43968),
    PARTY_PURPLE(51136),
    LUNAR(16652);

    public final int color;

    private Color(final int color){
        this.color = color;
    }
}
