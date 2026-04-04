package com.fot.system.config;

import java.awt.*;

public class AppTheme {
    // Primary Colors
    public static final Color PRIMARY = new Color(0, 128, 128);      // Teal
    public static final Color PRIMARY_HOVER = new Color(0, 150, 136);
    public static final Color PRIMARY_ACTIVE = new Color(0, 105, 105);

    // Background Colors
    public static final Color BG_LIGHT = Color.WHITE;
    public static final Color SIDEBAR_BG = PRIMARY;

    // Text Colors
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color TEXT_DARK = new Color(50, 50, 50);
    public static final Color TEXT_MUTED = new Color(180, 180, 180);

    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    // table
    public static final Color TABLE_HEADER_BG = PRIMARY; // Teal Color
    public static final Color TABLE_HEADER_FG = Color.WHITE; // Header text white
    public static final Color TABLE_ROW_BG = Color.WHITE;
    public static final Color TABLE_ROW_ALT_BG = new Color(245, 245, 245); // Light Gray for alternate rows
    public static final Color TABLE_SELECTION_BG = new Color(224, 242, 241); // Light Teal
    public static final Color TABLE_SELECTION_FG = new Color(0, 77, 64); // Dark Teal
}