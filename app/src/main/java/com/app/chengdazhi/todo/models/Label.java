package com.app.chengdazhi.todo.models;

/**
 * Created by chengdazhi on 10/1/15.
 */
public class Label {
    private int colorId;

    private String name;

    private int labelId;

    public Label(int colorId, String name) {
        this.colorId = colorId;
        this.name = name;
    }

    public Label(int colorId, String name, int labelId) {
        this.colorId = colorId;
        this.name = name;
        this.labelId = labelId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColorId() {
        return colorId;
    }

    public String getName() {
        return name;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }

    public int getLabelId() {
        return labelId;
    }

}
