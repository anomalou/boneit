package org.anomalou.view;

import org.anomalou.controller.ToolPanelController;
import org.anomalou.model.tools.Tool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Toolbar extends JPanel {
    private final UIManager uiManager;
    private final ToolPanelController toolPanelController;

    private JPanel content;

    public Toolbar(UIManager uiManager) {
        this.uiManager = uiManager;
        this.toolPanelController = uiManager.getToolPanelController();

        setupPanel();
        createToolbar();
    }

    private void createToolbar() {
        GridBagLayout layout = new GridBagLayout();
        content = new JPanel();
        ArrayList<Tool> tools = toolPanelController.getToolList();
        content.setLayout(layout);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL; //TODO need tests
        for (int i = 0; i < 2; i++) {
            content.add(createTool(tools.get(i)), constraints);
        }
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
        JButton toolButton = new JButton(tool.getName());
        toolButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        toolButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolPanelController.setCurrentTool(tool);
            }
        });

        return toolButton;
    }
}
