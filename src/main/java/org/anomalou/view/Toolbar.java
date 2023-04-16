package org.anomalou.view;

import org.anomalou.controller.ToolPanelController;
import org.anomalou.model.tools.Tool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Toolbar extends JPanel {
    private final UIManager uiManager;
    private final ToolPanelController toolPanelController;

    public Toolbar(UIManager uiManager){
        this.uiManager = uiManager;
        this.toolPanelController = uiManager.getToolPanelController();

        createToolbar();
    }

    private void createToolbar(){
        setupPanel();

        for(Tool tool : toolPanelController.getToolList()){
            add(createTool(tool));
        }

        add(Box.createGlue());
    }

    private void setupPanel(){
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }

    private Component createTool(Tool tool){
        Button toolButton = new Button(tool.getName());

        toolButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolPanelController.setCurrentTool(tool);
            }
        });

        return toolButton;
    }
}
