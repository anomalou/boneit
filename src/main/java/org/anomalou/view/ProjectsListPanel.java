package org.anomalou.view;

import org.anomalou.controller.ProjectsManagerController;
import org.anomalou.model.Project;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyPair;
import java.util.Map;

public class ProjectsListPanel extends JPanel {
    private UIManager uiManager;
    private ProjectsManagerController projectsManagerController;
    public ProjectsListPanel(UIManager uiManager){
        super();

        this.uiManager = uiManager;
        projectsManagerController = uiManager.getProjectsManagerController();

        initLayout();
        build();
    }

    private void initLayout(){
        setLayout(new GridBagLayout());
        projectsManagerController.scan();
    }

    private void build(){
        Map<String, String> projects = projectsManagerController.getProjects();

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0d;
        constraints.gridx = 0;

        for(Map.Entry<String, String> entry : projects.entrySet()){
            panel.add(createProjectButton(entry.getKey(), entry.getValue()), constraints);
        }

        constraints.weighty = 1.0d;
        constraints.gridheight = GridBagConstraints.REMAINDER;

        panel.add(new JPanel(), constraints);

        JButton createButton = new JButton("Create project");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectsManagerController.create("New project", 400, 400);
                uiManager.openSession();
            }
        });

        JButton configButton = new JButton("Config");

        GridBagConstraints contentConstraints = new GridBagConstraints();
        contentConstraints.fill = GridBagConstraints.BOTH;
        contentConstraints.gridx = 0;
        contentConstraints.gridwidth = 2;
        contentConstraints.weightx = 1.0d;
        contentConstraints.weighty = 1.0d;

        add(new JScrollPane(panel), contentConstraints);

        contentConstraints.gridwidth = 1;
        contentConstraints.weighty = 0.0d;

        add(createButton, contentConstraints);

        contentConstraints.gridx = 1;
        contentConstraints.weightx = 0.0d;

        add(configButton, contentConstraints);
    }

    private JButton createProjectButton(String name, String path){
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

        return button;
    }
}
