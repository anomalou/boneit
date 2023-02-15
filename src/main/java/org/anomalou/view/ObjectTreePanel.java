package org.anomalou.view;

import org.anomalou.controller.ObjectController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.model.Canvas;
import org.anomalou.model.Layer;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class ObjectTreePanel extends JPanel {

    private Canvas canvas;
    private ObjectController objectController;
    private PropertiesController propertiesController;

    private JTree tree;

    //Properties
    private int iconWidth;
    private int iconHeight;

    public ObjectTreePanel(Canvas canvas, ObjectController objectController, PropertiesController propertiesController) {
        super();
        this.canvas = canvas;
        this.objectController = objectController;
        this.propertiesController = propertiesController;

        loadProperties();
        createTree();
        createListeners();
    }

    private void createTree(){
        tree = new JTree(createNode("Scene", canvas.getLayersHierarchy()));
        tree.setCellRenderer(new ObjectTreeCellRenderer(iconWidth, iconHeight));
        setLayout(new BorderLayout());
        add(new JLabel("Scene tree"), BorderLayout.PAGE_START);
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    private DefaultMutableTreeNode createNode(Object nodeObject, ArrayList<UUID> objects){
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeObject);

        ArrayList<Layer> sortedObject = new ArrayList<>();

        objects.forEach(uuid -> {
            Layer object = objectController.getObject(uuid);
            sortedObject.add(object);
        });

        Collections.sort(sortedObject, Collections.reverseOrder());

        sortedObject.forEach(object -> {
            node.add(createNode(object, object.getChildren()));
        });

        return node;
    }

    private void createListeners(){
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                if(node == null)
                    return;

                Object object = node.getUserObject();
                if(object instanceof Layer){
                    canvas.setSelection(((Layer) object).getUuid());
                }

                getParent().repaint();
            }
        });
    }

    private void loadProperties(){
        iconWidth = propertiesController.getInt("preview.width");
        iconHeight = propertiesController.getInt("preview.height");
    }
}
