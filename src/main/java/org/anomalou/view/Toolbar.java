package org.anomalou.view;

import org.anomalou.controller.PropertiesController;
import org.anomalou.controller.ToolsManagerController;
import org.anomalou.model.tools.Palette;
import org.anomalou.model.tools.Tool;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Toolbar extends JPanel {
    private final UIManager uiManager;
    private final PropertiesController propertiesController;
    private final ToolsManagerController toolsManagerController;

    private JPanel content;

    private int iconScale;

    public Toolbar(UIManager uiManager) {
        this.uiManager = uiManager;
        this.propertiesController = uiManager.getPropertiesController();
        this.toolsManagerController = uiManager.getToolsManagerController();

        loadProperties();
        setupPanel();
        createToolbar();
    }

    private void createToolbar() {
        GridBagLayout layout = new GridBagLayout();
        content = new JPanel();
        ArrayList<Tool> tools = toolsManagerController.getToolList();
        content.setLayout(layout);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(5, 1, 5, 0);
        for (int i = 0; i < tools.size(); i++) {
            content.add(createTool(tools.get(i)), constraints);
        }

        content.add((createPalette()));

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 1.0d;
        content.add(new JPanel(), constraints);

        add(new JLabel("Toolbar"), BorderLayout.PAGE_START);
        add(content, BorderLayout.CENTER);
        add(new JSeparator(), BorderLayout.PAGE_END);
    }

    private void setupPanel() {
        setLayout(new BorderLayout());
    }

    private Component createTool(Tool tool) {
        JButton toolButton = new JButton();
        toolButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        toolButton.setIcon(new ImageIcon(tool.getIcon().getScaledInstance(iconScale, iconScale, Image.SCALE_FAST)));
        toolButton.setToolTipText(tool.getName());

        toolButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolsManagerController.setCurrentTool(tool);
            }
        });

        return toolButton;
    }

    private Component createPalette(){
        JPanel paletteContent = new JPanel();

        Palette palette = toolsManagerController.getPalette();

        JButton foreground = new JButton(" ");
        JButton background = new JButton(" ");

        foreground.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(foreground, "Select foreground", palette.getForegroundColor());
                if(color == null)
                    return;
                foreground.setBackground(color);
                palette.setForegroundColor(color);
            }
        });

        background.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(background, "Select background", palette.getBackgroundColor());
                if(color == null)
                    return;
                background.setBackground(color);
                palette.setBackgroundColor(color);
            }
        });

        foreground.setBackground(palette.getForegroundColor());
        background.setBackground(palette.getBackgroundColor());

        paletteContent.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0d;
        constraints.weighty = 1.0d;

        paletteContent.add(foreground, constraints);
        paletteContent.add(background, constraints);

        paletteContent.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0), "Palette"));

        return paletteContent;
    }

    private void loadProperties() {
        iconScale = propertiesController.getInt("toolicon.scale");
    }
}
