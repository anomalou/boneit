package org.anomalou.view;

import jdk.jshell.spi.ExecutionControl;
import org.anomalou.controller.CanvasController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.model.scene.Bone;
import org.anomalou.model.scene.Groupable;
import org.anomalou.model.scene.Layer;
import org.anomalou.model.scene.SceneObject;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class ObjectTreePanel extends JPanel {
    private final UIManager uiManager;
    private final CanvasController canvasController;
    private final PropertiesController propertiesController;

    private JTree tree;

    //Properties
    private int iconWidth;
    private int iconHeight;

    public ObjectTreePanel(UIManager uiManager) {
        super();
        this.uiManager = uiManager;
        this.canvasController = uiManager.getCanvasController();
        this.propertiesController = uiManager.getPropertiesController();

        loadProperties();
        createTree();
        createNodes();
        createListeners();
        createPopupMenu();
    }

    private void createTree(){
        tree = new JTree();
        tree.setCellRenderer(new ObjectTreeCellRenderer(iconWidth, iconHeight));
        tree.setShowsRootHandles(true);
        setLayout(new BorderLayout());
        add(new JLabel("Scene tree"), BorderLayout.PAGE_START);
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    private void createNodes(){
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        treeModel.setRoot(createNode("Scene", canvasController.getLayersHierarchy()));
    }

    private DefaultMutableTreeNode createNode(Object nodeObject, ArrayList<SceneObject> objects){
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeObject);

        ArrayList<SceneObject> sortedObject = new ArrayList<>(objects);

        sortedObject.sort(Collections.reverseOrder());

        sortedObject.forEach(object -> {
            if(object instanceof Groupable<?>)
                try{
                    node.add(createNode(object, ((Groupable<SceneObject>) object).getChildren()));
                }catch (Exception ex){
                    ex.printStackTrace();
                }
        });

        return node;
    }

    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode root, Object objectToCompare){
        if(root == null)
            return null;

        if(root.getUserObject().equals(objectToCompare)){
            return root;
        }

        DefaultMutableTreeNode node = null;

        int i = 0;

        while(node == null && i < root.getChildCount()){
            node = findNode((DefaultMutableTreeNode) root.getChildAt(i), objectToCompare);
            i++;
        }

        return node;
    }

    private void appendSelection(Object selection, Object object){
        DefaultMutableTreeNode selectedNode = findNode((DefaultMutableTreeNode) tree.getLastSelectedPathComponent(), selection);
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root;
        if(selection == null)
            root = (DefaultMutableTreeNode) treeModel.getRoot();
        else
            root = selectedNode;

        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(object);
        treeModel.insertNodeInto(newNode, root, 0);
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
                    canvasController.setSelection(((Layer) object).getUuid());
                    uiManager.updateInspector();
                }

                getParent().repaint();
            }
        });
    }

    private void createPopupMenu(){
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem newLayerItem = new JMenuItem("Create layer");
        newLayerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SceneObject selection = canvasController.getSelection();
                SceneObject newLayer = new Layer();
                canvasController.registerObject(newLayer);
                appendSelection(selection, newLayer);
            }
        });

        JMenuItem newBoneItem = new JMenuItem("Create bone");
        newBoneItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SceneObject selection = canvasController.getSelection();
                SceneObject newBone = new Bone();
                canvasController.registerObject(newBone);
                appendSelection(selection, newBone);
            }
        });

        popupMenu.add(newLayerItem);
        popupMenu.add(newBoneItem);
        popupMenu.add(new JToolBar.Separator());
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
                treeModel.removeNodeFromParent(findNode((DefaultMutableTreeNode) treeModel.getRoot(), canvasController.getSelection()));

                canvasController.unregisterObject(canvasController.getSelection());

                getParent().repaint();
            }
        });
        popupMenu.add(deleteItem);

        tree.setComponentPopupMenu(popupMenu);
    }

    private void loadProperties(){
        iconWidth = propertiesController.getInt("preview.width");
        iconHeight = propertiesController.getInt("preview.height");
    }
}
