package org.anomalou.view;

import org.anomalou.annotation.Editable;
import org.anomalou.annotation.EditorType;
import org.anomalou.controller.CanvasController;
import org.anomalou.model.scene.Layer;
import org.anomalou.model.scene.SceneObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class InspectorPanel extends JPanel {
    private final UIManager uiManager;
    private final CanvasController canvasController;

    private final JPanel container;

    public InspectorPanel(UIManager uiManager){
        this.uiManager = uiManager;
        this.canvasController = uiManager.getCanvasController();

        container = new JPanel();

        initialize();
    }

    private void initialize(){
        setLayout(new BorderLayout());
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        add(new JScrollPane(container), BorderLayout.CENTER);
    }

    public void updateFields(){
        if(canvasController.getSelection() != null)
            buildFieldsEditor();
        else{
            container.removeAll();
            revalidate();
        }
    }

    private void buildFieldsEditor(){
        container.removeAll();

        SceneObject selected = canvasController.getSelection();

        Field[] fields = unpackFields(selected.getClass());

        Box verticalBox = Box.createVerticalBox();

        for(Field f : fields){
            if(f.isAnnotationPresent(Editable.class)) {
                Component component = getFieldEditor(f, selected);
                if(component != null){
                    verticalBox.add(component);
                }
            }
        }

        verticalBox.add(Box.createGlue());

        container.add(verticalBox);

        revalidate();
    }

    private Field[] unpackFields(Class<?> clazz){
        if(clazz == null)
            return new Field[0];

        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(unpackFields(clazz.getSuperclass())));
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        return fields.toArray(new Field[]{});
    }

    private Component getFieldEditor(Field field, Object object){
        if(field.getAnnotation(Editable.class).editorType() == EditorType.TEXT_FIELD){
            return createTextField(field, object);
        }
        if(field.getAnnotation(Editable.class).editorType() == EditorType.CHECK_BOX){
            return createCheckBox(field, object);
        }
        if(field.getAnnotation(Editable.class).editorType() == EditorType.VECTOR_EDITOR){
            return createVectorField(field, object);
        }

        return null;
    }

    private void setFieldValue(Field field, Object object, Object newValue){
        try{
            field.setAccessible(true);
            field.set(object, newValue);
            field.setAccessible(false);
        }catch (IllegalAccessException ex){
            ex.printStackTrace();
        }
    }

    private Object getFieldValue(Field field, Object object){
        try{
            field.setAccessible(true);
            Object value = field.get(object);
            field.setAccessible(false);
            return value;
        }catch (IllegalAccessException ex){
            ex.printStackTrace();
        }

        return -1;
    }

    private Component createTextField(Field field, Object object){
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
                try{
                    if(field.getType() == String.class)
                        setFieldValue(field, object, textField.getText());
                    else if(field.getType() == Integer.class)
                        setFieldValue(field, object, Integer.valueOf(textField.getText()));
                    else if(field.getType() == Double.class)
                        setFieldValue(field, object, Double.valueOf(textField.getText()));
                    uiManager.updateCanvas();
                    uiManager.updateTree();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(fieldName));
        panel.add(textField);

        return panel;
    }

    private Component createCheckBox(Field field, Object object){
        JCheckBox checkBox = new JCheckBox("Enabled");

        String fieldName = field.getAnnotation(Editable.class).name();
        checkBox.setSelected((Boolean) getFieldValue(field, object));

        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFieldValue(field, object, checkBox.isSelected());
                uiManager.updateCanvas();
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(fieldName));
        panel.add(checkBox);

        return panel;
    }

    private Component createVectorField(Field field, Object object){
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
                try{
                    point.x = Integer.valueOf(xTextField.getText());
                    setFieldValue(field, object, point);
                    uiManager.updateCanvas();
                    uiManager.updateTree();
                }catch (Exception ex){
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
                try{
                    point.y = Integer.valueOf(yTextField.getText());
                    setFieldValue(field, object, point);
                    uiManager.updateCanvas();
                    uiManager.updateTree();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(fieldName));
        panel.add(xTextField);
        panel.add(yTextField);

        return panel;
    }
}
