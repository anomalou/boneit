package org.anomalou.view;

import org.anomalou.controller.ProjectsManagerController;

import javax.swing.*;
import javax.swing.plaf.IconUIResource;
import javax.swing.text.IconView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class ProjectsListPanel extends JPanel {
    private UIManager uiManager;
    private ProjectsManagerController projectsManagerController;
    public ProjectsListPanel(UIManager uiManager){
        super();

        this.uiManager = uiManager;
        projectsManagerController = uiManager.getProjectsManagerController();

        build();
    }

    private void build(){
        initLayout();
        createInterface();
    }

    private void initLayout(){
        setLayout(new GridBagLayout());
        projectsManagerController.scan();
    }

    private void createInterface(){
        Map<String, String> projects = projectsManagerController.getProjects();

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0d;
        constraints.gridx = 0;

        for(Map.Entry<String, String> entry : projects.entrySet()){
            panel.add(createProjectItem(entry.getKey(), entry.getValue()), constraints);
        }

        constraints.weighty = 1.0d;
        constraints.gridheight = GridBagConstraints.REMAINDER;

        panel.add(new JPanel(), constraints);

        GridBagConstraints contentConstraints = new GridBagConstraints();
        contentConstraints.fill = GridBagConstraints.BOTH;
        contentConstraints.gridx = 0;
        contentConstraints.gridwidth = 2;
        contentConstraints.weightx = 1.0d;
        contentConstraints.weighty = 1.0d;

        add(new JScrollPane(panel), contentConstraints);

        contentConstraints.gridwidth = 1;
        contentConstraints.weighty = 0.0d;

        add(createCreateButton(), contentConstraints);

        contentConstraints.gridx = 1;
        contentConstraints.weightx = 0.0d;

        add(createConfigButton(), contentConstraints);
    }

    private JPanel createProjectItem(String name, String path){
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());

        GridBagConstraints panelConst = new GridBagConstraints();
        panelConst.weightx = 1.0d;
        panelConst.weighty = 1.0d;
        panelConst.fill = GridBagConstraints.BOTH;

        JButton button = new JButton();

        button.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 2;

        JLabel nameLabel = new JLabel(name);
        Font font = nameLabel.getFont();
        nameLabel.setFont(new Font(font.getName(), Font.BOLD, 15));
        button.add(nameLabel, constraints);

        constraints.gridy = 2;
        constraints.gridheight = 1;

        JLabel pathLabel = new JLabel(path);
        pathLabel.setForeground(Color.lightGray);
        button.add(pathLabel, constraints);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectsManagerController.open(path);
                uiManager.openSession();
            }
        });

        panel.add(button, panelConst);

        //TODO
        JButton delete = new JButton("X");
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO
            }
        });


        return panel;
    }

    private JButton createCreateButton(){
        JButton createButton = new JButton("Create project");

        JPanel setupPanel = new JPanel();
        JTextField projectName = new JTextField();
        JSpinner width = new JSpinner(new SpinnerNumberModel());
        JSpinner height = new JSpinner(new SpinnerNumberModel());

        setupPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.weightx = 1.0d;
        constraints.gridwidth = 2;

        setupPanel.add(new JLabel("Project name:"), constraints);
        setupPanel.add(projectName, constraints);

        constraints.gridwidth = 1;

        setupPanel.add(new JLabel("Width:"), constraints);
        setupPanel.add(width, constraints);

        constraints.gridx = 1;

        setupPanel.add(new JLabel("Height:"), constraints);
        setupPanel.add(height, constraints);
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int answer = JOptionPane.showConfirmDialog(createButton, setupPanel, "New project", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if(answer == JOptionPane.CANCEL_OPTION || answer == JOptionPane.CLOSED_OPTION)
                    return;

                int w = (int) width.getValue();
                int h = (int) height.getValue();

                if(w <= 0 || h <= 0)
                    return;

                String name = projectName.getText();
                if(name.isEmpty() || name.isBlank())
                    name = "New project";

                projectsManagerController.create(name, w, h);
                uiManager.openSession();
            }
        });

        return createButton;
    }

    private JButton createConfigButton(){
        JButton configButton = new JButton("Config");

        return configButton;
    }
}
