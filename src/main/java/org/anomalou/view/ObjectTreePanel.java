package org.anomalou.view;

import org.anomalou.controller.CanvasController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.model.scene.Bone;
import org.anomalou.model.scene.Group;
import org.anomalou.model.scene.Layer;
import org.anomalou.model.scene.SceneObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.AbstractDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.tools.JavaFileManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

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

    private void createTree() {
        tree = new JTree();
        tree.setCellRenderer(new ObjectTreeCellRenderer(iconWidth, iconHeight));
        tree.setShowsRootHandles(true);
        setLayout(new BorderLayout());
        add(new JLabel("Scene tree"), BorderLayout.PAGE_START);
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    private void createNodes() {
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        treeModel.setRoot(createNode("Scene", canvasController.getLayersHierarchy()));
    }

    private DefaultMutableTreeNode createNode(Object nodeObject, ArrayList<SceneObject> objects) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeObject);

        if (objects == null)
            return node;

        ArrayList<SceneObject> sortedObject = new ArrayList<>(objects);

        sortedObject.sort(Collections.reverseOrder());

        sortedObject.forEach(object -> {
            if (object instanceof Group<?>) {
                try {
                    node.add(createNode(object, ((Group<SceneObject>) object).getChildren()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                node.add(createNode(object, null));
            }
        });

        return node;
    }

    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode root, Object objectToCompare) {
        if (root == null)
            return null;

        if (root.getUserObject().equals(objectToCompare)) {
            return root;
        }

        DefaultMutableTreeNode node = null;

        int i = 0;

        while (node == null && i < root.getChildCount()) {
            node = findNode((DefaultMutableTreeNode) root.getChildAt(i), objectToCompare);
            i++;
        }

        return node;
    }

    private void appendSelection(Object selection, Object object) {
        DefaultMutableTreeNode selectedNode = findNode((DefaultMutableTreeNode) tree.getLastSelectedPathComponent(), selection);
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root;
        if (selection == null)
            root = (DefaultMutableTreeNode) treeModel.getRoot();
        else
            root = selectedNode;

        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(object);
        treeModel.insertNodeInto(newNode, root, 0);
    }

    private void createListeners() {
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                if (node == null)
                    return;

                Object object = node.getUserObject();
                if (object instanceof SceneObject) {
                    canvasController.setSelection(((SceneObject) object).getUuid());
                    uiManager.updateInspector();
                }

                getParent().repaint();
            }
        });
    }

    private void createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenu newLayerMenu = new JMenu("Create layer...");
        JMenuItem newLayerItem = new JMenuItem("New empty");
        JMenuItem existLayerItem = new JMenuItem("Select exist");

        newLayerMenu.setMnemonic(KeyEvent.VK_1);
        newLayerItem.setMnemonic(KeyEvent.VK_1);
        existLayerItem.setMnemonic(KeyEvent.VK_2);

        JPanel dialogContent = new JPanel();
        JSpinner layerWidth = new JSpinner(new SpinnerNumberModel());
        layerWidth.setValue(canvasController.getWidth());
        JSpinner layerHeight = new JSpinner(new SpinnerNumberModel());
        layerHeight.setValue(canvasController.getHeight());

        dialogContent.setLayout(new GridBagLayout());
        layerWidth.setToolTipText("Width");
        layerHeight.setToolTipText("Height");

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0d;
        constraints.insets = new Insets(4, 4, 4, 4);

        dialogContent.add(new JLabel("Layer size (WxH):"), constraints);

        constraints.gridy = 1;
        constraints.gridwidth = 1;

        dialogContent.add(layerWidth, constraints);

        constraints.gridx = 1;

        dialogContent.add(layerHeight, constraints);

        newLayerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, dialogContent, "New layer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION)
                    return;

                int width = (int) layerWidth.getValue();
                int height = (int) layerHeight.getValue();

                if (width == 0 || height == 0) {
                    JOptionPane.showMessageDialog(null, "Width or height can't be 0!");
                    return;
                }

                layerWidth.setValue(0);
                layerHeight.setValue(0);

                SceneObject selection = canvasController.getSelection();
                SceneObject newLayer = new Layer(width, height);
                canvasController.registerObject(newLayer);
                canvasController.addObject(selection, newLayer);
                if(!(selection instanceof Group<?>))
                    selection = null;

                appendSelection(selection, newLayer);
            }
        });

        existLayerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (file != null) {
                        try {
                            SceneObject selection = canvasController.getSelection();
                            SceneObject newLayer = new Layer(ImageIO.read(file));
                            canvasController.registerObject(newLayer);
                            canvasController.addObject(selection, newLayer);
                            if(!(selection instanceof Group<?>))
                                selection = null;

                            appendSelection(selection, newLayer);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        JMenuItem newBoneItem = new JMenuItem("Create bone");

        newBoneItem.setMnemonic(KeyEvent.VK_2);

        newBoneItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SceneObject selection = canvasController.getSelection();
                SceneObject newBone = new Bone();
                canvasController.registerObject(newBone);
                canvasController.addObject(selection, newBone);
                if(!(selection instanceof Group<?>))
                    selection = null;

                appendSelection(selection, newBone);
            }
        });

        newLayerMenu.add(newLayerItem);
        newLayerMenu.add(existLayerItem);
        popupMenu.add(newLayerMenu);
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

    private void loadProperties() {
        iconWidth = propertiesController.getInt("preview.width");
        iconHeight = propertiesController.getInt("preview.height");
    }
}
