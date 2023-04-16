package org.anomalou.view;

import org.anomalou.model.scene.Layer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ObjectTreeCellRenderer extends DefaultTreeCellRenderer {

    private final int width;
    private final int height;

    public ObjectTreeCellRenderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        Object object = ((DefaultMutableTreeNode) value).getUserObject();

        if (object instanceof Layer layer) {
            setIcon(new ImageIcon(layer.getSourceBitmap().getScaledInstance(width, height, Image.SCALE_FAST)));
            setText(layer.toString());
        } else {
            setIcon(null);
            setText(value.toString());
        }

        return this;
    }
}
