package gui;

import java.awt.*;

public class ControllerRobot {
    private final GameMoved model;
    ControllerRobot(GameMoved model){
        this.model = model;
    }

    public void setChangesModel(Point point){
        model.setTargetPosition(point);
    }
    public void updateModel(){
        model.onModelUpdateEvent();
    }
}
