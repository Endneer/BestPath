package bestPath;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;


public class Controller {

    @FXML
    public AnchorPane drawing;

    private boolean connecting = false;

    private Router firstClickedRouter = null;

    private Router sourceRouter = null;
    private Router destinationRouter = null;

    private List<Router> visitedRouters = new ArrayList<>();
    private List<Router> frontier = new ArrayList<>();
    private List<Float> frontierWeights = new ArrayList<>();

    private int numberOfRouters = 0;

    public void calc(MouseEvent mouseEvent) {
        if (sourceRouter.isDestination()) drawing.getScene().getWindow().hide();
        visitedRouters.add(sourceRouter);
        frontier.addAll(sourceRouter.getConnectedRouters());
        frontierWeights.addAll(sourceRouter.getConnectedRoutersWeights());

        int smallestIndex = 0;
        for (int i = 0; i < frontier.size(); i++) {
            if (frontierWeights.get(i) < frontierWeights.get(smallestIndex)) {
                smallestIndex = i;
            }
        }
        Router router = frontier.get(smallestIndex);
        frontier.remove(smallestIndex);
        frontierWeights.remove(smallestIndex);
        frontier.addAll(router.getConnectedRouters());
        frontierWeights.addAll(router.getConnectedRoutersWeights());
        visitedRouters.add(router);
        for (int i = 0; i < router.getConnectedRouters().size(); i++) {
            if (visitedRouters.contains(router.getConnectedRouters().get(i))) {}
        }
        if (router.isDestination()) drawing.getScene().getWindow().hide();
    }


    public void addRouter(MouseEvent mouseEvent) {
        Router router = new Router((float) mouseEvent.getX(), (float) mouseEvent.getY());
//        router.connectedRouters.add(router);
//        router.connectedRoutersWeights.add((float) 0);
        router.setOnMousePressed(Event::consume);
        router.setOnContextMenuRequested(event -> {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem setSource = new MenuItem("Set Source");
            setSource.setOnAction(event1 -> sourceRouter = router);
            MenuItem setDestination = new MenuItem("Set Destination");
            setDestination.setOnAction(event1 -> destinationRouter = router);
            contextMenu.getItems().addAll(setSource, setDestination);
            contextMenu.show(router, event.getScreenX(), event.getScreenY());
        });
        router.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY)
                if (firstClickedRouter == null || connecting == false) {
                    firstClickedRouter = router;
                    connecting = true;
                } else if (connecting == true && firstClickedRouter != router && !firstClickedRouter.getConnectedRouters().contains(router)) {
                    router.getConnectedRouters().add(firstClickedRouter);
                    firstClickedRouter.getConnectedRouters().add(router);
                    Line line = new Line();
                    line.setStartX(firstClickedRouter.getCenterX());
                    line.setStartY(firstClickedRouter.getCenterY());
                    line.setEndX(router.getCenterX());
                    line.setEndY(router.getCenterY());
                    drawing.getChildren().add(0, line);
                    Label label = new Label("1");
                    label.setLayoutX((line.getStartX() + line.getEndX()) / 2);
                    label.setLayoutY((line.getStartY() + line.getEndY()) / 2);
                    TextInputDialog inputDialog = new TextInputDialog("1");
                    inputDialog.show();
                    inputDialog.setOnCloseRequest(event1 -> {
                        try {
                            label.setText(String.valueOf(Float.parseFloat(inputDialog.getEditor().getText())));
                        } catch (NumberFormatException exception) {
                        }
                        drawing.getChildren().add(label);
                    });
                    int index = firstClickedRouter.getConnectedRouters().indexOf(router);
                    firstClickedRouter.getConnectedRoutersWeights().add(index, Float.parseFloat(label.getText()));
                    index = router.getConnectedRouters().indexOf(firstClickedRouter);
                    router.getConnectedRoutersWeights().add(index, Float.parseFloat(label.getText()));
                    connecting = false;
                }
            event.consume();
        });
        Label routerLabel = new Label();
        routerLabel.setLayoutX(router.getLayoutX() + 10);
        routerLabel.setLayoutY(router.getLayoutY() - 20);
        drawing.getChildren().add(router);
        numberOfRouters++;
        routerLabel.setText("router " + numberOfRouters);
        drawing.getChildren().add(routerLabel);
    }

    private class Router extends ImageView {

        private List<Router> connectedRouters;
        private List<Float> connectedRoutersWeights;

        public ArrayList<Float> getConnectedRoutersWeights() {
            return (ArrayList<Float>) connectedRoutersWeights;
        }

        public float getCenterX() {
            return x;
        }

        public float getCenterY() {
            return y;
        }

        float x;
        float y;

        public Router(float x, float y) {
            this.x = x;
            this.y = y;
            Image image = new Image(getClass().getResourceAsStream("router-symbol.png"));
            setImage(image);
            setLayoutX(x - image.getWidth() / 2);
            setLayoutY(y - image.getHeight() / 2);
            setCursor(Cursor.HAND);
            connectedRouters = new ArrayList<>();
            connectedRoutersWeights = new ArrayList<>();
        }

        public List<Router> getConnectedRouters() {
            return connectedRouters;
        }

        public boolean isDestination() {
            return this == destinationRouter ? true : false;
        }
    }
}