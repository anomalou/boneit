package org.anomalou.view;

import org.anomalou.controller.ObjectController;
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

    private JTree tree;

    public ObjectTreePanel(Canvas canvas, ObjectController objectController) {
        super();
        this.canvas = canvas;
        this.objectController = objectController;

        createTree();
        createListeners();
    }

    private void createTree(){
        tree = new JTree(createNode("Scene", canvas.getLayersHierarchy()));
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
            }
        });
    }
}
