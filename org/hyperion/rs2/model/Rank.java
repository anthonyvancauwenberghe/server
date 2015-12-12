package org.hyperion.rs2.model;

import org.hyperion.rs2.util.TextUtils;

/**
 * Created by DeviousPK on 10/05/14.
 */
public enum Rank {

    PLAYER("@bla@"), // 0
    HERO("@bla@"), // 1
    LEGEND("@bla@"), // 2
    VETERAN("@ffb226@"), // 3
    DONATOR("@ba0000@"), // 4
    SUPER_DONATOR("@03ce10@"), // 5
    WIKI_EDITOR("@bla@"), // 6
    EVENT_MANAGER("@bla@"), // 7
    HELPER("@e519c0@"), // 8
    FORUM_MODERATOR("@blu@"), // 9
    MODERATOR("@blu@"), // 10
    GLOBAL_MODERATOR("@blu@"), // 11
    COMMUNITY_MANAGER("@cya@"), // 12
    HEAD_MODERATOR("@00cccc@"), // 13
    ADMINISTRATOR("@dbl@"), // 14
    DEVELOPER("@6F0095@"), // 15
    OWNER("@FFFFFF@"); // 16

    public static int shift = 0;

    static {
        shift = Rank.values().length + 8;
    }

    private final long bitMask;
    private final String yellColor;

    Rank(final String yellColor) {
        this.bitMask = 1L << ordinal();
        this.yellColor = yellColor;
    }

    public static Rank forIndex(final long index) {
        if(index >= values().length)
            return PLAYER;
        return values()[((int) index)];
    }


	/* Static methods */

    public static long setPrimaryRank(final Player player, final Rank rank) {
        return setPrimaryRank(player.getPlayerRank(), rank);
    }

    public static long setPrimaryRank(final long r, final Rank rank) {
        return setPrimaryRank(r, rank, false);
    }

    public static long setPrimaryRank(final Player player, final Rank rank, final boolean removeCurrentRankAbility) {
        return setPrimaryRank(player.getPlayerRank(), rank, removeCurrentRankAbility);
    }

    public static long setPrimaryRank(long r, final Rank rank, final boolean removeCurrentRankAbility) {
        if(removeCurrentRankAbility)
            r = Rank.removeAbility(r, Rank.forIndex((r >> shift)));
        r = r & ~((r >> shift) << shift);
        r = r | (rank.ordinal() << shift);
        return Rank.addAbility(r, rank);
    }

    public static Rank getPrimaryRank(final Player player) {
        return getPrimaryRank(player.getPlayerRank());
    }

    public static Rank getPrimaryRank(final long r) {
        return Rank.forIndex(getPrimaryRankIndex(r));
    }

    public static long getPrimaryRankIndex(final Player player) {
        return player.getPlayerRank() >> shift;
    }

    public static long getPrimaryRankIndex(final long r) {
        return r >> shift;
    }

    public static long addAbility(final Player player, final Rank rank) {
        return addAbility(player.getPlayerRank(), rank);
    }

    public static long addAbility(final long r, final Rank rank) {
        if(isAbilityToggled(r, rank))
            return r;
        return r | rank.getBitMask();
    }

    public static long removeAbility(final Player player, final Rank rank) {
        return removeAbility(player.getPlayerRank(), rank);
    }

    public static long removeAbility(long r, final Rank rank) {
        if(rank == Rank.PLAYER)
            return r;
        if(getPrimaryRank(r) == rank)
            r = setPrimaryRank(r, Rank.PLAYER);
        return r & ~rank.getBitMask();
    }

    public static boolean hasAbility(final Player player, final Rank... ranks) {
        return player != null && hasAbility(player.getPlayerRank(), ranks);
    }

    public static boolean hasAbility(final long r, final Rank... ranks) {
        if(isAbilityToggled(r, Rank.OWNER))
            return true;
        for(final Rank rank : ranks){
            if(rank.ordinal() < Rank.ADMINISTRATOR.ordinal()){
                if(hasAbility(r, ADMINISTRATOR))
                    return true;
            }
            if(rank.ordinal() >= Rank.HELPER.ordinal()){
                for(int i = Rank.OWNER.ordinal(); i >= rank.ordinal(); i--){
                    if(isAbilityToggled(r, Rank.forIndex(i)))
                        return true;
                }
            }
            if(rank == Rank.DONATOR){
                if(isAbilityToggled(r, Rank.SUPER_DONATOR))
                    return true;
            }
            if(rank == Rank.HERO){
                if(isAbilityToggled(r, Rank.LEGEND))
                    return true;
            }
            if(isAbilityToggled(r, rank))
                return true;
        }
        return false;
    }

    public static boolean isAbilityToggled(final Player player, final Rank rank) {
        return (player.getPlayerRank() & rank.getBitMask()) == rank.getBitMask();
    }

    public static boolean isAbilityToggled(final long r, final Rank rank) {
        return (r & rank.getBitMask()) == rank.getBitMask();
    }

    public static boolean isStaffMember(final Player player) {
        return isStaffMember(player.getPlayerRank());
    }

    public static boolean isStaffMember(final long r) {
        for(final Rank rank : Rank.values()){
            if(rank.ordinal() >= Rank.HELPER.ordinal()){
                if(hasAbility(r, rank))
                    return true;
            }
        }
        return false;
    }

    public long getBitMask() {
        return bitMask;
    }

    public String getYellColor() {
        return yellColor;
    }

    @Override
    public String toString() {
        String name = super.toString();
        name = name.replace("_", " ");
        name = TextUtils.titleCase(name, true);
        name = name.replaceAll("Super", "S.").replaceAll("Head", "H.").replaceAll("Forum", "F.").replaceAll("Community", "Comm.");
        return name;
    }
}


