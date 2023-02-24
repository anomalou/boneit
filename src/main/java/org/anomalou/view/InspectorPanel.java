package org.anomalou.view;

import org.anomalou.annotation.Editable;
import org.anomalou.annotation.EditorType;
import org.anomalou.controller.CanvasController;
import org.anomalou.model.Bone;
import org.anomalou.model.Layer;

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
    private final CanvasController canvasController;

    private final JPanel container;

    public InspectorPanel(CanvasController canvasController){
        this.canvasController = canvasController;

        container = new JPanel();

        initialize();
        debugButton();//TODO debug
    }

    private void initialize(){
        setLayout(new BorderLayout());
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        add(new JScrollPane(container), BorderLayout.CENTER);
    }

    //TODO debug? remove in future
    private void debugButton(){
        JButton debugUpdate = new JButton("DEBUG");
        debugUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildFieldsEditor();
            }
        });

        add(debugUpdate, BorderLayout.PAGE_START);
    }

    private void buildFieldsEditor(){
        container.removeAll();

        Layer selected = canvasController.getSelection();

        Field[] fields = unpackFields(selected.getClass());

        for(Field f : fields){
            if(f.isAnnotationPresent(Editable.class))
                selectFieldType(f, selected);
        }

        revalidate();
    }

    private Field[] unpackFields(Class<?> clazz){
        if(clazz == null)
            return new Field[0];

        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(unpackFields(clazz.getSuperclass())));
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        return fields.toArray(new Field[]{});
    }

    private void selectFieldType(Field field, Object object){
        if(field.getAnnotation(Editable.class).editorType() == EditorType.TEXT_FIELD){
            createTextField(field, object);
        }
        if(field.getAnnotation(Editable.class).editorType() == EditorType.CHECK_BOX){
            createCheckBox(field, object);
        }
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

    private void createTextField(Field field, Object object){
        JLabel fieldName = new JLabel();
        JTextField textField = new JTextField();

        fieldName.setText(field.getAnnotation(Editable.class).name());
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
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        container.add(fieldName);
        container.add(textField);
    }

    private void createCheckBox(Field field, Object object){
        JLabel fieldName = new JLabel();
        JCheckBox checkBox = new JCheckBox("Enabled");

        fieldName.setText(field.getAnnotation(Editable.class).name());
        checkBox.setSelected((Boolean) getFieldValue(field, object));

        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFieldValue(field, object, checkBox.isSelected());
            }
        });

        container.add(fieldName);
        container.add(checkBox);
    }
}
