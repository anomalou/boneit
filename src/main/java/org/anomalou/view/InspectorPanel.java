package org.anomalou.view;

import org.anomalou.annotation.Editable;
import org.anomalou.annotation.EditorType;
import org.anomalou.controller.CanvasController;
import org.anomalou.model.scene.Layer;
import org.anomalou.model.scene.SceneObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class InspectorPanel extends JPanel {
    private final UIManager uiManager;
    private final CanvasController canvasController;

    private final JPanel container;

    public InspectorPanel(UIManager uiManager) {
        this.uiManager = uiManager;
        this.canvasController = uiManager.getCanvasController();

        container = new JPanel();

        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        add(new JLabel("Inspector"), BorderLayout.PAGE_START);
        container.setLayout(new GridBagLayout());
        add(new JScrollPane(container), BorderLayout.CENTER);
    }

    public void updateFields() {
        container.removeAll();

        if (canvasController.getSelection() != null)
            buildFieldsEditor();

        revalidate();
    }

    private void buildFieldsEditor() {
        SceneObject selected = canvasController.getSelection();

        Field[] fields = unpackFields(selected.getClass());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0d;
        constraints.gridx = 0;
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isAnnotationPresent(Editable.class)) {
                JPanel component = getFieldEditor(fields[i], selected);
                if (component != null) {
                    component.setBorder(BorderFactory.createTitledBorder(new EmptyBorder(0, 0, 0, 0), fields[i].getAnnotation(Editable.class).name()));
                    component.setToolTipText(fields[i].getAnnotation(Editable.class).description());
                    container.add(component, constraints);
                }
            }
        }

        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.weighty = 1.0d;
        container.add(new JPanel(), constraints);
    }

    private Field[] unpackFields(Class<?> clazz) {
        if (clazz == null)
            return new Field[0];

        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(unpackFields(clazz.getSuperclass())));
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        return fields.toArray(new Field[]{});
    }

    private JPanel getFieldEditor(Field field, Object object) {
        if (field.getAnnotation(Editable.class).editorType() == EditorType.TEXT_FIELD) {
            return createTextField(field, object);
        }
        if (field.getAnnotation(Editable.class).editorType() == EditorType.CHECK_BOX) {
            return createCheckBox(field, object);
        }
        if (field.getAnnotation(Editable.class).editorType() == EditorType.VECTOR_EDITOR) {
            return createVectorField(field, object);
        }
        if(field.getAnnotation(Editable.class).editorType() == EditorType.IMAGE_PREVIEW){
            return createImagePreview(field, object);
        }

        return null;
    }

    private void setFieldValue(Field field, Object object, Object newValue) {
        try {
            field.setAccessible(true);
            field.set(object, newValue);
            field.setAccessible(false);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    private Object getFieldValue(Field field, Object object) {
        try {
            field.setAccessible(true);
            Object value = field.get(object);
            field.setAccessible(false);
            return value;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return -1;
    }

    private JPanel createTextField(Field field, Object object) {
        JTextField textField = new JTextField();

        String fieldName = field.getAnnotation(Editable.class).name();
        textField.setText(getFieldValue(field, object).toString());

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setText(getFieldValue(field, object).toString());
            }
        });
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (field.getType() == String.class)
                        setFieldValue(field, object, textField.getText());
                    else if (field.getType() == Integer.class)
                        setFieldValue(field, object, Integer.valueOf(textField.getText()));
                    else if (field.getType() == Double.class)
                        setFieldValue(field, object, Double.valueOf(textField.getText()));
                    uiManager.updateCanvas();
                    uiManager.updateTree();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(textField);

        return panel;
    }

    private JPanel createCheckBox(Field field, Object object) {
        JCheckBox checkBox = new JCheckBox("Enabled");

        String fieldName = field.getAnnotation(Editable.class).name();
        checkBox.setSelected((Boolean) getFieldValue(field, object));

        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFieldValue(field, object, checkBox.isSelected());
                uiManager.updateCanvas();
                uiManager.updateTree();
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(checkBox);

        return panel;
    }

    private JPanel createVectorField(Field field, Object object) {
        JTextField xTextField = new JTextField();
        JTextField yTextField = new JTextField();

        String fieldName = field.getAnnotation(Editable.class).name();
        Point point = (Point) ((Point) getFieldValue(field, object)).clone();

        xTextField.setText(String.valueOf(point.x));
        yTextField.setText(String.valueOf(point.y));

        xTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                Point point = (Point) getFieldValue(field, object);
                xTextField.setText(String.valueOf(point.x));
            }
        });
        xTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    point.x = Integer.valueOf(xTextField.getText());
                    setFieldValue(field, object, point);
                    uiManager.updateCanvas();
                    uiManager.updateTree();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        yTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                Point point = (Point) getFieldValue(field, object);
                yTextField.setText(String.valueOf(point.y));
            }
        });
        yTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    point.y = Integer.valueOf(yTextField.getText());
                    setFieldValue(field, object, point);
                    uiManager.updateCanvas();
                    uiManager.updateTree();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(xTextField);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(yTextField);

        return panel;
    }

    private JPanel createImagePreview(Field field, Object object){
        JPanel content = new JPanel();
        BufferedImage image = (BufferedImage) getFieldValue(field, object);
        ImagePreview preview = new ImagePreview(image);

        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0d;
        constraints.weighty = 1.0d;

        content.add(preview, constraints);
        return content;
    }
}
