package com.fot.system.view.dashboard.student;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TimetableDayBar extends JPanel {

    private final Color primaryColor = new Color(10, 143, 143);
    private final Color textDark = new Color(30, 30, 30);

    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private final Map<String, JButton> dayButtons = new HashMap<>();
    private final Consumer<String> onDaySelected;

    private String selectedDay = "Monday";

    public TimetableDayBar(Consumer<String> onDaySelected) {
        this.onDaySelected = onDaySelected;

        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        setBackground(new Color(245, 247, 250));

        for (String day : days) {
            JButton button = new JButton(day);
            styleButton(button, day.equals(selectedDay));

            button.addActionListener(e -> {
                selectedDay = day;
                updateButtonStyles();
                onDaySelected.accept(day);
            });

            dayButtons.put(day, button);
            add(button);
        }
    }

    public void setSelectedDay(String day) {
        this.selectedDay = day;
        updateButtonStyles();
    }

    private void updateButtonStyles() {
        for (String day : days) {
            JButton button = dayButtons.get(day);
            if (button != null) {
                styleButton(button, day.equals(selectedDay));
            }
        }
    }

    private void styleButton(JButton button, boolean selected) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 38));

        if (selected) {
            button.setBackground(primaryColor);
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setBorderPainted(false);
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(textDark);
            button.setOpaque(true);
            button.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        }
    }
}