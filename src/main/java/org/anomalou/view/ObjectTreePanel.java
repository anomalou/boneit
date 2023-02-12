package org.anomalou.view;

import org.anomalou.controller.ObjectController;
import org.anomalou.model.Canvas;
import org.anomalou.model.Layer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class ObjectTreePanel extends JPanel {

    private Canvas canvas;
    private ObjectController objectController;

    private JTree tree;

    public ObjectTreePanel(Canvas canvas, ObjectController objectController) {
        super();
        this.canvas = canvas;
        this.objectController = objectController;

        createTree();
    }

    private void createTree(){
        tree = new JTree(createNode("Scene", canvas.getLayersHierarchy()));
        add(new JScrollPane(tree), BorderLayout.PAGE_END);
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
}
