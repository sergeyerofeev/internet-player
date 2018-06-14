package sample.controllers;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javazoom.jl.player.Player;
import sample.extra.ErrorAlert;
import sample.model.InternetStream;
import sample.model.ListWrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;


public class MainController {
    private final double SCALE_ZOOM = 1.2;      // коэффициент увеличения
    private final double SCALE_NORMAL = 1.0;    // возврат к нормальным размерам

    private boolean PLAY_IMAGE = true;          // если true установлена картинка play.png
    private boolean LIST_VISIBLE = false;       // в начальный момент список воспроизведения не виден
    private Node[] imageArray;
    private ScaleTransition animImage;
    private Player player;
    private String urlStream;
    private InternetStream internetStream;
    private Parent root;
    private Stage primaryStage;

    // окно редактирования
    private EditController editController;
    private Stage editStage;

    // определяем название файла, куда будем сохранять список адресов интернет потоков
    private String xmlFileName = "internetStream.xml";

    // создаём список адресов интернет потоков
    private ObservableList<InternetStream> list = FXCollections.observableArrayList();

    @FXML
    private AnchorPane container;
    @FXML
    private ImageView btnPlay;
    @FXML
    private ImageView btnList;
    @FXML
    private ImageView btnMin;
    @FXML
    private ImageView btnExit;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private TableView<InternetStream> tableView;
    @FXML
    private TableColumn<InternetStream, String> tableColumn;

    @FXML
    public void initialize() {
        final double DURATION = 150;        // время анимации в мс

        animImage = new ScaleTransition(Duration.millis(DURATION));
        imageArray = new Node[]{btnPlay, btnList, btnMin, btnExit};

        // заполняем список из файла XML
        loadPersonDataFromFile(xmlFileName);
//        list.add(new InternetStream("Chilaut", "http://air.radiorecord.ru:8102/chil_128"));
//        list.add(new InternetStream("Вести ФМ", "http://icecast.vgtrk.cdnvideo.ru/vestifm_mp3_64kbps"));
//        list.add(new InternetStream("Relax FM", "http://ic2.101.ru:8000/c19_1"));

        tableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        // заполняем элемент tableView
        tableView.setItems(list);
        tableView.getStyleClass().add("noheader");
        // при запуске прячем элемент tableView и кнопки управления листом
        container.setVisible(LIST_VISIBLE);
        // присваиваем переменной urlStream верхнее значение из списка воспроизведения
        urlStream = list.get(0).getUrlData();

        // подключаем прослушивание изменения выбора в списке listView
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
            // если выбрана хотя бы одна запись в листе, делаем активной кнопку Delete
            if(btnDelete.isDisable() || btnEdit.isDisable()) {
                btnDelete.setDisable(false);
                btnEdit.setDisable(false);
            }
            if(!urlStream.equals(newValue.getUrlData())) {
                urlStream = newValue.getUrlData();
                if(PLAY_IMAGE) {
                    player.close();
                    player_thread(urlStream);
                }
            }
            internetStream = newValue;
        });

        player_thread(urlStream);

        btnPlay.addEventHandler(MouseEvent.ANY, event -> {
            if(event.getEventType() == MouseEvent.MOUSE_ENTERED){
                zoomImage(btnPlay, SCALE_ZOOM);
            }
            if(event.getEventType() == MouseEvent.MOUSE_EXITED){
                zoomImage(btnPlay, SCALE_NORMAL);
            }
            if(event.getEventType() == MouseEvent.MOUSE_CLICKED){
                btnPlay.setImage((PLAY_IMAGE)? new Image("resources/play.png"): new Image("resources/pause.png"));
                PLAY_IMAGE ^= true;
                if(PLAY_IMAGE){
                    player_thread(urlStream);
                } else {
                    player.close();
                }
            }
            if(event.getEventType() == MouseEvent.MOUSE_DRAGGED){
                event.consume();
            }
        });

        btnList.addEventHandler(MouseEvent.ANY, event -> {
            if(event.getEventType() == MouseEvent.MOUSE_ENTERED){
                zoomImage(btnList, SCALE_ZOOM);
            }
            if(event.getEventType() == MouseEvent.MOUSE_EXITED){
                zoomImage(btnList, SCALE_NORMAL);
            }
            if(event.getEventType() == MouseEvent.MOUSE_CLICKED){
                LIST_VISIBLE ^= true;
                container.setVisible(LIST_VISIBLE);
            }
            if(event.getEventType() == MouseEvent.MOUSE_DRAGGED){
                event.consume();
            }
        });

        btnMin.addEventHandler(MouseEvent.ANY, event -> {
            if(event.getEventType() == MouseEvent.MOUSE_ENTERED){
                zoomImage(btnMin, SCALE_ZOOM);
            }
            if(event.getEventType() == MouseEvent.MOUSE_EXITED){
                zoomImage(btnMin, SCALE_NORMAL);
            }
            if(event.getEventType() == MouseEvent.MOUSE_CLICKED){
                btnMin.setScaleX(SCALE_NORMAL);
                btnMin.setScaleY(SCALE_NORMAL);
                //Stage stage = (Stage)scene.getScene().getWindow();
                primaryStage.setIconified(true);
            }
            if(event.getEventType() == MouseEvent.MOUSE_DRAGGED){
                event.consume();
            }
        });

        btnExit.addEventHandler(MouseEvent.ANY, event -> {
            if(event.getEventType() == MouseEvent.MOUSE_ENTERED){
                zoomImage(btnExit, SCALE_ZOOM);
            }
            if(event.getEventType() == MouseEvent.MOUSE_EXITED){
                zoomImage(btnExit, SCALE_NORMAL);
            }
            if(event.getEventType() == MouseEvent.MOUSE_CLICKED){
                if(player != null) {
                    player.close();
                }
                //Stage stage = (Stage)scene.getScene().getWindow();
                primaryStage.close();
            }
            if(event.getEventType() == MouseEvent.MOUSE_DRAGGED){
                event.consume();
            }
        });

    }

    // при наведении увеличиваем картинку, в остальных случаях возвращаем к нормальным размерам
    private void zoomImage(Node value, double scale){
        if(scale == SCALE_ZOOM){
            // при быстром дввижении указателя мыши по картинкам может получуться что элемент принимает промежуточное
            // значение Scale, для возврата к исходному состоянию используем следующий код
            for(Node item: imageArray){
                if(value == item){
                    continue;
                }
                if(item.getScaleX() != SCALE_NORMAL){
                    item.setScaleX(SCALE_NORMAL);
                    item.setScaleY(SCALE_NORMAL);
                }
            }
        }
        animImage.stop();
        animImage.setNode(value);
        animImage.setToX(scale);
        animImage.setToY(scale);
        animImage.play();
    }

    // создаём поток в котором будет работать наш плайер
    private void player_thread(String url_string){
        Thread thread = new Thread(()->{
            try {
                URL url = new URL(url_string);
                InputStream fin = url.openStream();
                InputStream is = new BufferedInputStream(fin);

                player = new Player(is);
                System.out.println("поток запущен");
                player.play();
                System.out.println("поток остановлен");
            } catch (Exception  e) {
                System.out.println("поток завершён по ошибке");
                System.out.println(e.toString());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void delete() {
        int index = tableView.getSelectionModel().getSelectedIndex();
        if(index != -1) {
            list.remove(index);
            savePersonDataToFile(xmlFileName);
        }
    }

    public void edit() {
        String oldDescription = internetStream.getDescription();
        String oldUrlData = internetStream.getUrlData();
        editController.setTextDescription(oldDescription);
        editController.setTextUrl(oldUrlData);
        editController.setInternetStream(internetStream);
        showEditDialog();
        // если поля диалогового окна пусты, восстанавливаем старые значения
        if(editController.getInternetStream().getDescription().equals("") ||
                editController.getInternetStream().getUrlData().equals("")) {
            internetStream.setDescription(oldDescription);
            internetStream.setUrlData(oldUrlData);
            return;
        }
        // если поля диалогового окна остались без изменения данные не сохраняем
        if(editController.getInternetStream().getDescription().equals(oldDescription) &&
                editController.getInternetStream().getUrlData().equals(oldUrlData)) {
            return;
        }
        savePersonDataToFile(xmlFileName);
    }

    public void add() {
        editController.setInternetStream(new InternetStream());
        editController.setTextDescription("");
        editController.setTextUrl("");
        showEditDialog();
        // если поля диалогового окна не заполнены данные не сохраняем
        if(editController.getInternetStream().getDescription().equals("") ||
                editController.getInternetStream().getUrlData().equals("")) {
            return;
        }
        list.add(editController.getInternetStream());
        savePersonDataToFile(xmlFileName);
    }

    // передаём Stage из класса Main в MainController
    public void setStage(Stage primaryStage) {
       this.primaryStage = primaryStage;
    }

    // инициализаируем EditController и передаём root окна редактирования
    public void setEditController(EditController editController, Parent root) {
        this.editController = editController;
        this.root = root;
    }

    private void showEditDialog() {
        if(editStage == null) {
            // создаём окно редактирования
            editStage = new Stage();
            editController.setStage(editStage);
            editStage.setResizable(false);
            editStage.initStyle(StageStyle.TRANSPARENT);
            editStage.setScene(new Scene(root));
            editStage.initModality(Modality.WINDOW_MODAL);
            editStage.initOwner(primaryStage);
            // показываем и скрываем окно, только при такой комбинации мы можем получить
            // первоначальные editStage.Width() и editStage.height()
            editStage.show();
            editStage.hide();
        }
        // располагаем окно редактирования по центру главного окна
        editStage.setX(primaryStage.getX() + primaryStage.getWidth()/2 - editStage.getWidth()/2);
        editStage.setY(primaryStage.getY() + primaryStage.getHeight()/2 - editStage.getHeight()/2);
        // показываем окно и останавливаем главный поток для ожидания результатов выбора пользователя
        editStage.showAndWait();
    }



    /**
     * Загружает информацию из указанного файла
     */
    private void loadPersonDataFromFile(String xmlFileName) {
        File file;
        try {
            JAXBContext context = JAXBContext.newInstance(ListWrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            file = new File(xmlFileName);
            // Чтение XML из файла и демаршализация
            ListWrapper wrapper = (ListWrapper) unmarshaller.unmarshal(file);

            list.clear();
            list.addAll(wrapper.getList());
        } catch (Exception e) { // catches ANY exception
            ErrorAlert errorAlert = new ErrorAlert(primaryStage,"Произошла ошибка при загрузке данных,\nперезапустите приложение!");
            (errorAlert.getErrorStage()).showAndWait();
            System.exit(1);
        }
    }

    /**
     * Сохраняет текущую информацию в указанном файле
     */
    private void savePersonDataToFile(String xmlFileName) {
        try {
            JAXBContext context = JAXBContext.newInstance(ListWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Обёртываем наши данные об адресатах.
            ListWrapper wrapper = new ListWrapper();
            wrapper.setList(list);

            // Маршаллируем и сохраняем XML в файл.
            marshaller.marshal(wrapper, new File(xmlFileName));
        } catch (Exception e) {
            System.out.println("sef");
            ErrorAlert errorAlert = new ErrorAlert(primaryStage,"Произошла ошибка при сохранении данных,\nперезапустите приложение!");
            (errorAlert.getErrorStage()).showAndWait();
        }
    }
}
