package cluescrolleditor.res;

import javax.swing.*;

public final class Res {

    public static final ImageIcon SCROLL_16 = icon("scroll", 16);
    public static final ImageIcon SCROLL_ADD_32 = icon("scroll_add", 32);
    public static final ImageIcon SCROLL_DELETE_32 = icon("scroll_delete", 32);
    public static final ImageIcon SCROLL_SAVE_32 = icon("scroll_save", 32);

    public static final ImageIcon LOCK_ADD_32 = icon("lock_add", 32);
    public static final ImageIcon LOCK_DELETE_32 = icon("lock_delete", 32);
    public static final ImageIcon LOCK_16 = icon("lock", 16);

    public static final ImageIcon REWARD_ADD_32 = icon("reward_add", 32);
    public static final ImageIcon REWARD_DELETE_32 = icon("reward_delete", 32);
    public static final ImageIcon REWARD_16 = icon("reward", 16);

    private Res(){}

    private static ImageIcon icon(final String name, final int size){
        return new ImageIcon(Res.class.getResource(String.format("img/%s_%d.png", name, size)));
    }
}
