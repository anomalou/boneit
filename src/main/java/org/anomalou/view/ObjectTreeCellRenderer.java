package org.anomalou.view;

import com.formdev.flatlaf.ui.FlatRoundBorder;
import org.anomalou.model.scene.Layer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ObjectTreeCellRenderer extends DefaultTreeCellRenderer {

    private final int width;
    private final int height;

    private JButton button;

    public ObjectTreeCellRenderer(int width, int height) {
        super();

        this.width = width;
        this.height = height;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        Object object = ((DefaultMutableTreeNode) value).getUserObject();

        if (object instanceof Layer layer) {
            setIcon(new ImageIcon(layer.getSourceBitmap().getScaledInstance(width, height, Image.SCALE_FAST)));
            String text = "";
            if (!layer.isVisible())
                setForeground(Color.lightGray);
            text += layer.toString();
            setText(text);
        } else {
            setIcon(null);
            setText(value.toString());
        }

        return this;
    }
}
