package com.fot.system.view.dashboard.lecturer.myCourses;

import com.fot.system.config.AppTheme;
import com.fot.system.model.entity.*;
import com.fot.system.view.shared_components.MaterialActionButton;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

public class CourseMaterialCard extends JPanel {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * create material card with only open action
     * @param material material entity
     * @param onOpen open action callback
     * @author poornika
     */
    public CourseMaterialCard(CourseMaterial material, Runnable onOpen) {
        this(material, onOpen, null, null);
    }

    /**
     * create material card with optional actions
     * @param material material entity
     * @param onOpen open action callback
     * @param onEdit edit action callback
     * @param onDelete delete action callback
     * @author poornika
     */
    public CourseMaterialCard(CourseMaterial material, Runnable onOpen, Runnable onEdit, Runnable onDelete) {
        setLayout(new BorderLayout(14, 0));
        setBackground(AppTheme.CARD_MUTED_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.CARD_BORDER, 1, false),
                new EmptyBorder(14, 14, 14, 14)
        ));
        setPreferredSize(new Dimension(0, 118));
        setMinimumSize(new Dimension(0, 118));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));

        add(createFileIcon(material), BorderLayout.WEST);
        add(createTextContent(material), BorderLayout.CENTER);
        add(createActions(onOpen, onEdit, onDelete), BorderLayout.EAST);
    }

    /**
     * create left-side file type icon
     * @param material material entity
     * @author poornika
     */
    private JComponent createFileIcon(CourseMaterial material) {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(52, 52));

        JLabel iconLabel = new JLabel(FontIcon.of(resolveFileIcon(material), 20, AppTheme.FILE_ICON_FG));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(AppTheme.FILE_ICON_BG);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(44, 44));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        wrap.add(iconLabel);
        return wrap;
    }

    /**
     * create card text content area
     * @param material material entity
     * @author poornika
     */
    private JComponent createTextContent(CourseMaterial material) {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(material.getTitle());
        title.setFont(AppTheme.fontBold(15));
        title.setForeground(AppTheme.TEXT_DARK);

        JTextArea body = new JTextArea(buildBody(material));
        body.setEditable(false);
        body.setLineWrap(true);
        body.setWrapStyleWord(true);
        body.setOpaque(false);
        body.setFont(AppTheme.fontPlain(13));
        body.setForeground(AppTheme.TEXT_SUBTLE);
        body.setRows(2);

        JLabel meta = new JLabel(buildMeta(material));
        meta.setFont(AppTheme.fontPlain(12));
        meta.setForeground(AppTheme.TEXT_MUTED);

        content.add(title);
        content.add(Box.createVerticalStrut(6));
        content.add(body);
        content.add(Box.createVerticalStrut(8));
        content.add(meta);
        return content;
    }

    /**
     * create right-side action button area
     * @param onOpen open action callback
     * @param onEdit edit action callback
     * @param onDelete delete action callback
     * @author poornika
     */
    private JComponent createActions(Runnable onOpen, Runnable onEdit, Runnable onDelete) {
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        if (onOpen != null) {
            MaterialActionButton openButton = new MaterialActionButton(
                    FontAwesomeSolid.EXTERNAL_LINK_ALT,
                    AppTheme.ACTION_ICON_FG,
                    AppTheme.ACTION_ICON_BG,
                    AppTheme.ACTION_ICON_HOVER,
                    "Open Material"
            );
            openButton.addActionListener(e -> onOpen.run());
            actions.add(openButton);
        }

        if (onEdit != null) {
            MaterialActionButton editButton = new MaterialActionButton(
                    FontAwesomeSolid.EDIT,
                    AppTheme.ACTION_ICON_FG,
                    AppTheme.ACTION_ICON_BG,
                    AppTheme.ACTION_ICON_HOVER,
                    "Edit Material"
            );
            editButton.addActionListener(e -> onEdit.run());
            actions.add(editButton);
        }

        if (onDelete != null) {
            MaterialActionButton deleteButton = new MaterialActionButton(
                    FontAwesomeSolid.TRASH_ALT,
                    AppTheme.ACTION_DELETE_ICON_FG,
                    AppTheme.ACTION_DELETE_ICON_BG,
                    AppTheme.ACTION_DELETE_ICON_HOVER,
                    "Remove Material"
            );
            deleteButton.addActionListener(e -> onDelete.run());
            actions.add(deleteButton);
        }

        return actions;
    }

    /**
     * build description text with safe fallback
     * @param material material entity
     * @author poornika
     */
    private static String buildBody(CourseMaterial material) {
        String description = material.getDescription() == null || material.getDescription().trim().isEmpty()
                ? "No description provided."
                : material.getDescription().trim();
        return description;
    }

    /**
     * build metadata line shown at card footer
     * @param material material entity
     * @author poornika
     */
    private static String buildMeta(CourseMaterial material) {
        String uploadedAt = material.getUploadedAt() == null ? "-" : DATE_FORMAT.format(material.getUploadedAt());
        String uploadedBy = material.getUploadedByName() == null || material.getUploadedByName().trim().isEmpty()
                ? "-"
                : material.getUploadedByName().trim();
        String fileType = material.getFileType() == null || material.getFileType().trim().isEmpty()
                ? "File"
                : material.getFileType().trim();
        return fileType + "  |  Uploaded: " + uploadedAt + "  |  By: " + uploadedBy;
    }

    /**
     * resolve icon by material file type
     * @param material material entity
     * @author poornika
     */
    private FontAwesomeSolid resolveFileIcon(CourseMaterial material) {
        String fileType = material.getFileType() == null ? "" : material.getFileType().trim().toLowerCase();
        if (fileType.equals("pdf")) {
            return FontAwesomeSolid.FILE_PDF;
        }
        if (fileType.equals("jpg") || fileType.equals("jpeg") || fileType.equals("png")) {
            return FontAwesomeSolid.IMAGE;
        }
        if (fileType.equals("doc") || fileType.equals("docx")) {
            return FontAwesomeSolid.FILE_WORD;
        }
        if (fileType.equals("ppt") || fileType.equals("pptx")) {
            return FontAwesomeSolid.FILE_POWERPOINT;
        }
        if (fileType.equals("xls") || fileType.equals("xlsx")) {
            return FontAwesomeSolid.FILE_EXCEL;
        }
        if (fileType.equals("zip") || fileType.equals("rar")) {
            return FontAwesomeSolid.FILE_ARCHIVE;
        }
        return FontAwesomeSolid.FILE_ALT;
    }
}
