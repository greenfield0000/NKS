package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.HemmingNet;
import utils.FileUtil;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static javafx.scene.control.ScrollPane.ScrollBarPolicy.ALWAYS;

public class HemmingNetUiController implements Initializable {
    public AnchorPane initializationTab;
    public TabPane mainPane;
    public Tab trainingPane;
    public HBox viewSimplesPane;
    public AnchorPane viewMainSimplesPane;
    public ScrollPane viewFindSimple;
    public ScrollPane viewFoundSimple;
    public Tab weightingCoefficientMatrixView;
    public Tab scalesFeedbackMatrixView;
    public AnchorPane weightingCoefficientMatrix;
    public AnchorPane scalesFeedbackMatrixMatrix;
    public AnchorPane graphic;
    public Tab graphicTab;
    private HemmingNet net;
    SingleSelectionModel<Tab> selectionModel;
    List<Integer> coefficient;
    List<List<Integer>> coefficientList = new ArrayList<>();
    int numberBinarySigns;
    int numberNeurons;

    @FXML
    public TextField numberNeuronsField;
    @FXML
    public TextField numberBinarySignsField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        net = new HemmingNet();
        selectionModel = mainPane.getSelectionModel();
        coefficient = new ArrayList<>();
    }

    @FXML
    private void initializationNetAction() {
        if (numberBinarySignsField.getText().isEmpty() || numberNeuronsField.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Не заполнены входные данные !").show();
            return;
        }
        numberBinarySigns = Integer.parseInt(numberBinarySignsField.getText());
        numberNeurons = Integer.parseInt(numberNeuronsField.getText());
        net.setNumberBinarySigns((int) Math.pow(numberBinarySigns, 2));
        net.setNumberNeurons(numberNeurons);
        initializationTab.setDisable(true);
        trainingPane.setDisable(false);
        selectionModel.select(1);
    }

    /**
     * Добавление нового образца
     */
    public void addSimpleAction() {
        if (viewSimplesPane.getChildren().size() < numberNeurons) {
            createDialog();
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Добавлено максимальное количество образцов");
        }
    }

    /**
     * Добавление нового образа "Диалоговое окно" при простой инициализации
     */
    private void createDialog() {
        Stage dialog = new Stage();
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        Control input = createInputPane(numberBinarySigns);
        Button buttonOk = new Button("Ok");
        buttonOk.setOnAction(event -> {
            net.addReferenceSample(new ArrayList<>(coefficient));
            coefficientList.add(new ArrayList<>(coefficient));
            dialog.close();
            input.getChildrenUnmodifiable().stream().forEach(it -> it.setDisable(true));
            viewSimplesPane.getChildren().add(input);
        });
        Button buttonEx = new Button("Close");
        buttonEx.setOnAction(evt -> dialog.close());
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(buttonOk, buttonEx);
        box.getChildren().addAll(input, buttons);
        Scene scene = new Scene(box, (numberBinarySigns * 40) + 100, (numberBinarySigns * 40) + 100);
        dialog.setScene(scene);
        dialog.show();
    }


    private Control createInputPane(int numberBinarySigns) {
//        coefficientList.add(coefficient);
        coefficient.clear();
        int columns, rows;
        columns = rows = numberBinarySigns;
        GridPane grid = new GridPane();
        grid.getStyleClass().add("game-grid");

        for (int i = 0; i < columns; i++) {
            ColumnConstraints column = new ColumnConstraints(42);
            grid.getColumnConstraints().add(column);
        }

        for (int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints(42);
            grid.getRowConstraints().add(row);
        }

        int count = -1;
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                Pane pane = new Pane();
                ++count;
                int finalCount = count;
                pane.setOnMouseReleased(event -> {
                    if (event.getButton().equals(MouseButton.SECONDARY)) {
                        coefficient.set(finalCount, -1);
                        pane.getChildren().add(getRectangle(Color.WHITE));
                    }
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        coefficient.set(finalCount, 1);
                        pane.getChildren().add(getRectangle(Color.BLACK));
                    }
                });
                coefficient.add(-1);
                pane.getChildren().add(getRectangle(Color.WHITE));
                pane.getStyleClass().add("game-grid-cell");
                if (j == 0) {
                    pane.getStyleClass().add("first-column");
                }
                if (i == 0) {
                    pane.getStyleClass().add("first-row");
                }
                grid.add(pane, i, j);
            }
        }
        ScrollPane sp = new ScrollPane();
        sp.setContent(grid);
        sp.setHbarPolicy(ALWAYS);
        sp.setVbarPolicy(ALWAYS);
        return sp;
    }

    /**
     * Закрашиваем цветом квадратик
     *
     * @param color
     * @return
     */
    private static Node getRectangle(Color color) {
        Rectangle rectangle = new Rectangle(1, 1, 39, 39);
        rectangle.setFill(color);
        return new Group(rectangle);
    }

    /**
     * Удаление образа
     */
    public void deletedSimpleAction() {
        Stage dialog = new Stage();
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        TextField input = new TextField();
        Button buttonOk = new Button("Ok");
        buttonOk.setOnAction(event -> {
            int index = Integer.parseInt(input.getText());
            dialog.close();
            viewSimplesPane.getChildren().remove(index);
            net.getReferenceSamplesMatrix().remove(index);
        });
        Button buttonEx = new Button("Close");
        buttonEx.setOnAction(evt -> dialog.close());
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(buttonOk, buttonEx);
        box.getChildren().addAll(new Label("Введите индекс"), input, buttons);
        Scene scene = new Scene(box, 100, 100);
        dialog.setScene(scene);
        dialog.show();
    }

    /**
     * Обучение сети.
     */
    public void teachingNetAction() {
        if (viewSimplesPane.getChildren().size() == numberNeurons) {
            net.teachTheNetwork();
            //trainingPane.setDisable(true);
            selectionModel.select(2);
            selectionModel.getSelectedItem().setDisable(false);
            weightingCoefficientMatrixView.setDisable(false);
            scalesFeedbackMatrixView.setDisable(false);
            viewMainSimplesPane.getChildren().stream().forEach(it -> {
                if (it instanceof Button) {
                    it.setDisable(true);
                }
            });
            completeTableWeightingCoefficient();
            completeTableScalesFeedbackMatrix();
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Добавлены не все образцы");
        }
    }

    private void completeTableScalesFeedbackMatrix() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("game-grid");

        for (int i = 0; i < numberNeurons; i++) {
            ColumnConstraints column = new ColumnConstraints(42);
            grid.getColumnConstraints().add(column);
        }

        for (int i = 0; i < numberNeurons; i++) {
            RowConstraints row = new RowConstraints(42);
            grid.getRowConstraints().add(row);
        }

        List<List<Double>> scalesFeedback = net.getScalesFeedbackMatrix();
        List<Double> scales;
        for (int i = 0; i < numberNeurons; i++) {
            scales = scalesFeedback.get(i);
            for (int j = 0; j < scales.size(); j++) {
                Pane pane = new Pane();
                Label label = new Label(String.format("%.2f", scales.get(j)));
                label.setLayoutY(10);
                label.setLayoutX(10);
                pane.getChildren().add(label);
                pane.getStyleClass().add("game-grid-cell");
                if (j == 0) {
                    pane.getStyleClass().add("first-column");
                }
                if (i == 0) {
                    pane.getStyleClass().add("first-row");
                }
                grid.add(pane, j, i);
            }
        }
        ScrollPane sp = new ScrollPane();
        sp.setContent(grid);
        sp.setHbarPolicy(ALWAYS);
        sp.setVbarPolicy(ALWAYS);
        scalesFeedbackMatrixMatrix.getChildren().add(sp);
    }

    private void completeTableWeightingCoefficient() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("game-grid");

        for (int i = 0; i < Math.pow(numberBinarySigns, 2); i++) {
            ColumnConstraints column = new ColumnConstraints(42);
            grid.getColumnConstraints().add(column);
        }

        for (int i = 0; i < numberNeurons; i++) {
            RowConstraints row = new RowConstraints(42);
            grid.getRowConstraints().add(row);
        }

        List<List<Double>> weightingCoefficient = net.getWeightingCoefficientMatrix();
        List<Double> weighting;
        for (int i = 0; i < numberNeurons; i++) {
            weighting = weightingCoefficient.get(i);
            for (int j = 0; j < weighting.size(); j++) {
                Pane pane = new Pane();
                Label label = new Label(weighting.get(j).toString());
                label.setLayoutY(10);
                label.setLayoutX(10);
                pane.getChildren().add(label);
                pane.getStyleClass().add("game-grid-cell");
                if (j == 0) {
                    pane.getStyleClass().add("first-column");
                }
                if (i == 0) {
                    pane.getStyleClass().add("first-row");
                }
                grid.add(pane, j, i);
            }
        }
        ScrollPane sp = new ScrollPane();
        sp.setContent(grid);
        sp.setHbarPolicy(ALWAYS);
        sp.setVbarPolicy(ALWAYS);
        weightingCoefficientMatrix.getChildren().add(sp);
    }

    /**
     * Распознать
     */
    public void runNetAction() {
        Integer resultVector = net.runNet();
        if (resultVector == -1) {
            new Alert(Alert.AlertType.INFORMATION, "Извините, не могу определить").show();
        } else {
            viewFoundSimple.setContent(createOutputPane(net.getReferenceSamplesMatrix().get(resultVector)));
        }
        graphicTab.setDisable(false);
        createGraphic();
    }

    /**
     * Построение графика
     */
    private void createGraphic() {
        graphic.getChildren().clear();
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Номер итерации");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Ошибка на данной итерации");
        //creating the chart
        LineChart<Number, Number> lineChart =
                new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("График сходимости сети");
        //defining a series
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Ошибка");
        //populating the series with data
        int index = 0;
        for (Double error : net.getErrors()) {
            ++index;
            series.getData().add(new XYChart.Data<>(index, error));
        }

        lineChart.getData().add(series);
        graphic.getChildren().add(lineChart);
    }

    /**
     * Построение
     *
     * @param outputVector
     * @return
     */
    private Node createOutputPane(List<Integer> outputVector) {
        coefficient.clear();
        int columns, rows;
        columns = rows = numberBinarySigns;
        GridPane grid = new GridPane();
        grid.getStyleClass().add("game-grid");

        for (int i = 0; i < columns; i++) {
            ColumnConstraints column = new ColumnConstraints(42);
            grid.getColumnConstraints().add(column);
        }

        for (int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints(42);
            grid.getRowConstraints().add(row);
        }

        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                Pane pane = new Pane();
                int index = i * rows + j;
                pane.getChildren().add(outputVector.get(index) == -1 ? getRectangle(Color.WHITE) : getRectangle(Color.BLACK));
                pane.getStyleClass().add("game-grid-cell");
                if (j == 0) {
                    pane.getStyleClass().add("first-column");
                }
                if (i == 0) {
                    pane.getStyleClass().add("first-row");
                }
                grid.add(pane, i, j);
            }
        }
        grid.setDisable(true);
        return grid;
    }

    /**
     * Распознай !!!
     */
    public void sendFindSimpleAction() {
        viewFoundSimple.setContent(null);
        Stage dialog = new Stage();
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        Control input = createInputPane(numberBinarySigns);
        Button buttonOk = new Button("Ok");
        buttonOk.setOnAction(event -> {
            net.submitInputVector(new ArrayList<>(coefficient));
            dialog.close();
            input.setDisable(true);
            viewFindSimple.setContent(input);
        });
        Button buttonEx = new Button("Close");
        buttonEx.setOnAction(evt -> dialog.close());
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(buttonOk, buttonEx);
        box.getChildren().addAll(input, buttons);
        Scene scene = new Scene(box, (numberBinarySigns * 40) + 100, (numberBinarySigns * 40) + 100);
        dialog.setScene(scene);
        dialog.show();
    }

    /**
     * Завершение работы
     *
     * @param actionEvent
     */
    public void exitProg(ActionEvent actionEvent) {
        System.exit(0);
    }

    /**
     * Загрузить данные из файла
     *
     * @param actionEvent
     */
    public void loadFormFile(ActionEvent actionEvent) throws IOException, ParserConfigurationException {
        FileUtil fileUtil = new FileUtil();
        fileUtil.setFileName("NeuroNetDataStruct.xml");
        HashMap<String, Object> resMap = (HashMap<String, Object>) fileUtil.loadFile();
        prepareDataFromLoadFile(resMap);
    }

    /**
     * Передача загруженных данных в сеть
     *
     * @param resMap - подгруженные данные
     */
    private void prepareDataFromLoadFile(HashMap<String, Object> resMap) {
        // если произошла ошибка загрузки, то ничего не делаем
        if (resMap.get("isSuccess").equals(false)) {
            new Alert(Alert.AlertType.WARNING, "Не удалось загрузить файл!");
            return;
        }
        createInputPaneFromLoad(resMap);

    }

    /**
     * Инициализация загруженных данных
     *
     * @param resMap - load data from file
     */
    private void createInputPaneFromLoad(HashMap<String, Object> resMap) {
        numberNeuronsField.setText((String) resMap.get("numberNeuronsField"));
        numberBinarySignsField.setText((String) resMap.get("numberBinarySignsField"));
        coefficientList = (List<List<Integer>>) resMap.get("coefficientList");


        numberBinarySigns = Integer.parseInt(numberBinarySignsField.getText());
        numberNeurons = Integer.parseInt(numberNeuronsField.getText());
        net.setNumberBinarySigns((int) Math.pow(numberBinarySigns, 2));
        net.setNumberNeurons(numberNeurons);
        initializationTab.setDisable(true);
        trainingPane.setDisable(false);
        selectionModel.select(1);

        for (List<Integer> coefList : coefficientList) {

            int columns, rows;
            columns = rows = numberBinarySigns;
            GridPane grid = new GridPane();
            grid.getStyleClass().add("game-grid");

            for (int i = 0; i < columns; i++) {
                ColumnConstraints column = new ColumnConstraints(42);
                grid.getColumnConstraints().add(column);
                RowConstraints row = new RowConstraints(42);
                grid.getRowConstraints().add(row);
            }

            Pane pane = null;

            for (int i = 0; i < columns; i++) {
                for (int j = 0; j < rows; j++) {
                    pane = new Pane();

                    int index = i * rows + j;

                    if (coefList.get(index).equals(-1)) {
                        pane.getChildren().add(getRectangle(Color.WHITE));
                    } else {
                        pane.getChildren().add(getRectangle(Color.BLACK));
                    }

                    pane.getStyleClass().add("game-grid-cell");
                    if (j == 0) {
                        pane.getStyleClass().add("first-column");
                    }
                    if (i == 0) {
                        pane.getStyleClass().add("first-row");
                    }
                    grid.add(pane, i, j);
                }
            }

            ScrollPane sp = new ScrollPane();
            sp.setContent(grid);
            sp.setHbarPolicy(ALWAYS);
            sp.setVbarPolicy(ALWAYS);

            net.addReferenceSample(new ArrayList<>(coefList));
            sp.getChildrenUnmodifiable().stream().forEach(it -> it.setDisable(true));
            viewSimplesPane.getChildren().add(sp);
        }
        new Alert(Alert.AlertType.INFORMATION, "Данные успешно загружены.").show();
    }

    /**
     * Сохранить в файл образы
     *
     * @param actionEvent
     * @throws ParserConfigurationException
     */
    public void saveToFile(ActionEvent actionEvent) throws ParserConfigurationException {
        FileUtil fileUtil = new FileUtil();
        try {
            fileUtil.setFileName("NeuroNetDataStruct.xml");
            fileUtil.saveFile(numberNeuronsField.getText(),
                    numberBinarySignsField.getText(), coefficientList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Alert(Alert.AlertType.INFORMATION, "Данные успешно сохранены!").show();
    }
}
