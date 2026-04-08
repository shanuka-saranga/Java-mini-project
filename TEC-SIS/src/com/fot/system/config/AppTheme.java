package com.fot.system.config;

import java.awt.*;

public class AppTheme {

    // Primary Colors
    public static final Color PRIMARY = new Color(0, 128, 128);      // Teal
    public static final Color BASE_COLOR = PRIMARY;
    public static final Color PRIMARY_HOVER = new Color(0, 150, 136);
    public static final Color PRIMARY_ACTIVE = new Color(0, 105, 105);

    // Background Colors
    public static final Color BG_LIGHT = Color.WHITE;
    public static final Color SIDEBAR_BG = PRIMARY;
    public static final Color SURFACE_MUTED = new Color(248, 251, 251);
    public static final Color SURFACE_SOFT = new Color(245, 248, 248);

    // Text Colors
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color TEXT_DARK = new Color(50, 50, 50);
    public static final Color TEXT_MUTED = new Color(180, 180, 180);
    public static final Color TEXT_SUBTLE = new Color(110, 110, 110);

    // Borders / Frames
    public static final Color BORDER_LIGHT = new Color(235, 235, 235);
    public static final Color BORDER_MUTED = new Color(220, 220, 220);
    public static final Color BORDER_SOFT = new Color(240, 240, 240);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color CARD_MUTED_BG = new Color(248, 251, 251);
    public static final Color CARD_BORDER = new Color(225, 233, 233);
    public static final Color AVATAR_BORDER = PRIMARY;
    public static final Color AVATAR_BG = new Color(245, 248, 248);
    public static final Color AVATAR_TEXT = new Color(110, 110, 110);
    public static final Color PRIORITY_HIGH = new Color(211, 47, 47);
    public static final Color PRIORITY_MEDIUM = new Color(255, 152, 0);
    public static final Color PRIORITY_LOW = PRIMARY;
    public static final Color ICON_ACCENT = new Color(0, 121, 107);
    public static final Color CLOSE_ACTION = new Color(198, 40, 40);
    public static final Color CLOSE_ACTION_HOVER = new Color(229, 57, 53);
    public static final Color FILE_ICON_BG = new Color(224, 242, 241);
    public static final Color FILE_ICON_FG = PRIMARY;
    public static final Color ACTION_ICON_BG = new Color(241, 247, 247);
    public static final Color ACTION_ICON_HOVER = new Color(224, 242, 241);
    public static final Color ACTION_ICON_FG = PRIMARY;
    public static final Color ACTION_DELETE_ICON_BG = new Color(255, 235, 238);
    public static final Color ACTION_DELETE_ICON_HOVER = new Color(255, 205, 210);
    public static final Color ACTION_DELETE_ICON_FG = new Color(198, 40, 40);

    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    // table
    public static final Color TABLE_HEADER_BG = PRIMARY;
    public static final Color TABLE_HEADER_FG = Color.WHITE;
    public static final Color TABLE_ROW_BG = Color.WHITE;
    public static final Color TABLE_ROW_ALT_BG = new Color(245, 245, 245); // Light Gray for alternate rows
    public static final Color TABLE_SELECTION_BG = new Color(224, 242, 241); // Light Teal
    public static final Color TABLE_SELECTION_FG = new Color(0, 77, 64); // Dark Teal

    // 1. Primary Button (Save / Confirm)
    public static final Color BTN_SAVE_BG = PRIMARY;
    public static final Color BTN_SAVE_FG = Color.WHITE;
    public static final Color BTN_SAVE_HOVER = new Color(0, 105, 105); // Darker Teal

    // 2. Secondary Button (Edit / Update)
    public static final Color BTN_EDIT_BG = new Color(224, 242, 241); // Very Light Teal
    public static final Color BTN_EDIT_FG = PRIMARY;
    public static final Color BTN_EDIT_HOVER = new Color(178, 223, 219); // Slightly Darker Light Teal

    // 3. Danger/Neutral Button (Cancel / Reset)
    public static final Color BTN_CANCEL_BG = new Color(158, 158, 158); // Gray
    public static final Color BTN_CANCEL_FG = Color.WHITE;
    public static final Color BTN_CANCEL_HOVER = new Color(117, 117, 117); // Darker Gray

    // 4. Delete Button
    public static final Color BTN_DELETE_BG = new Color(211, 47, 47); // Red
    public static final Color BTN_DELETE_FG = Color.WHITE;
    public static final Color BTN_DELETE_HOVER = new Color(183, 28, 28);
}
