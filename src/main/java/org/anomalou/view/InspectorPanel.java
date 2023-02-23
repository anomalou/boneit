package org.anomalou.view;

import org.anomalou.annotation.Coordinates;
import org.anomalou.annotation.Editable;
import org.anomalou.annotation.Value;
import org.anomalou.controller.CanvasController;
import org.anomalou.model.Layer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class InspectorPanel extends JPanel {
    private CanvasController canvasController;

    private JPanel container;

    public InspectorPanel(CanvasController canvasController){
        this.canvasController = canvasController;

        container = new JPanel();

        configurePanel();
        debugButton();//TODO debug
    }

    private void configurePanel(){
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
                buildTuneElements();
            }
        });

        add(debugUpdate, BorderLayout.PAGE_START);
    }

    private void buildTuneElements(){
        container.removeAll();

        Layer selected = canvasController.getSelection();

        Field[] fields = unpackFields(selected.getClass());

        for(Field f : fields){
            if(f.isAnnotationPresent(Editable.class))
                buildFieldEditor(f);
        }

        revalidate();
    }

    private Field[] unpackFields(Class clazz){
        if(clazz == null)
            return new Field[0];

        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(unpackFields(clazz.getSuperclass())));
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        return fields.toArray(new Field[]{});
    }

    private void buildFieldEditor(Field field){
        if(field.isAnnotationPresent(Value.class)){
            createValueEditor(field);
        }
        if(field.isAnnotationPresent(Coordinates.class)){
            createCoordinateEditor(field);
        }
    }

    private void createValueEditor(Field field){
        JLabel fieldName = new JLabel();
        JTextField textField = new JTextField();

        try{
            fieldName.setText(field.getName());
            field.setAccessible(true);
            Object value = field.get(canvasController.getSelection());
            textField.setText(value.toString());
            field.setAccessible(false);
        }catch (IllegalAccessException ex){
            //TODO
        }

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                field.setAccessible(true);
//                field.set(canvasController.getSelection(), textField.getText());
            }
        });

        container.add(fieldName);
        container.add(textField);
    }

    private void createCoordinateEditor(Field field){

    }
}
