import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;
import jdk.jfr.Category;

import javafx.animation.PathTransition;
import javafx.event.EventHandler;
import javax.swing.*;
import javafx.event.ActionEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;

abstract class Actor{
    protected double Health;
    protected double Attackpow;
    protected int[] pos = new int[2];
    protected String[] Category = new String[2];
    protected ImageView actorImage;
    protected Timeline setUpTime;


}

class Plants extends Actor{
    protected long lastAdded;
    public Plants(){
        Category[0] = "plants";
    }

    public void setLayout(Node e,int pos[]){
        e.setLayoutX(pos[0]);
        e.setLayoutY(pos[1]);
    }
}

class PeaShooter extends Plants {
    private long lastShot = 0;
    private Timeline setUpTime;
    private backYard lawn;
    private bullet b;

    public PeaShooter(int pos[], backYard x) throws FileNotFoundException {
        Category[1] = "peashooter";
        actorImage = new ImageView(new Image(new FileInputStream("PlantsVsZombies_Images/peaShooter.gif"),120,120,false,false));
        setupTimeLine();
        lawn = x;
        setLayout(actorImage,pos);
        lawn.getBackYard().getChildren().add(actorImage);
        this.pos = pos;
    }

    public void setupTimeLine(){
        KeyFrame newKey = new KeyFrame(Duration.seconds(1), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }

    public void act(){
        long time = System.currentTimeMillis();
        if(time>lastShot+5000){
            b = new bullet(pos);
            lawn.getBackYard().getChildren().add(2,b.bulletImage);
            lastShot = time;
        }
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            act();
            if(lawn.pausePlay)
                b.shoot.pause();
            else
                b.shoot.play();

        }
    }
}

class bullet{
    ImageView bulletImage;
    private int startPos;
    private double speed = 5;
    TranslateTransition shoot;

    public bullet(int[] pos){
        try{
            bulletImage = new ImageView(new Image(new FileInputStream("PlantsVsZombies_Images/pea.png"),24,20,false,false));
            bulletImage.setLayoutX(pos[0]+60);
            bulletImage.setLayoutY(pos[1]+40);
            move();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void move(){
        shoot = new TranslateTransition();
        shoot.setDuration(Duration.millis(speed*1000));
        shoot.setNode(bulletImage);
        shoot.setToX(1366);
        shoot.setCycleCount(1);
        shoot.play();
    }
}

class CherryBomb extends Plants{}
class SunFlower extends Plants{}
class WallNut extends Plants{}
class PotatoMine extends Plants{}


class Zombies extends Actor{
    protected Timeline setUpTime;
    protected int speed;
    protected backYard lawn;
    public Zombies(){
        Category[0] = "Zombies";
    }

    public void setLayout(Node e,int x,int y){
        e.setLayoutY(y);
        e.setLayoutX(x);
    }
}

class NormalZombie extends Zombies{
    TranslateTransition zombieMove;

    public NormalZombie(int yCo, backYard x){
        try{
            Category[1] = "NormalZombie";
            pos[0] = 1200;
            pos[1] = yCo;
            actorImage = new ImageView(new Image(new FileInputStream("PlantsVsZombies_Images/normalZombieWalking.gif"),61,150,false,false));
            speed = 50;
            setLayout(actorImage,pos[0],pos[1]);
            move();
            lawn = x;
            setupTimeLine();
        } catch (FileNotFoundException f) {
            System.out.println(f.getMessage());
        }
    }

    public void setupTimeLine(){
        KeyFrame newKey = new KeyFrame(Duration.seconds(1), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }

    public void move(){
        zombieMove = new TranslateTransition();
        zombieMove.setDuration(Duration.millis(speed*1000));
        zombieMove.setNode(actorImage);
        zombieMove.setToX(-1000);
        System.out.println(actorImage.getX());
        zombieMove.setCycleCount(1);
        zombieMove.play();
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            if(lawn.pausePlay)
                zombieMove.pause();
            else
                zombieMove.play();
        }
    }
}

class FlagZombie extends Zombies{
    public FlagZombie() throws FileNotFoundException{
        Category[1] = "FlagZombie";
        actorImage = new ImageView(new Image(new FileInputStream("PlantsVsZombies_Images/normalZombieWalking.gif"),81,130,false,false));
    }
}

class ConeHeadZombie extends Zombies{
    public ConeHeadZombie() throws  FileNotFoundException{
        Category[2] = "ConeHeadZombie";
        actorImage = new ImageView(new Image(new FileInputStream("PlantsVsZombies_Images/coneheadZombieWalking.gif"),81,130,false,false));
    }
}

class PoleVaultingZombie extends Zombies{
    public PoleVaultingZombie() throws FileNotFoundException{
        Category[3] = "PoleVaultingZombie";
        actorImage = new ImageView(new Image(new FileInputStream("PlantsVsZombies_Images/footballZombieWalking.gif"),81,130,false,false));
    }
}

class generateMap{
    private int[] firstRowX;
    private int[] firstRowY;
    private int[] addX;
    public generateMap(int nosOfPatches){
            firstRowX = new int[nosOfPatches];
            firstRowY = new int[nosOfPatches];
    }

    public int[][] getFirstRow(int mt,int type){
        for (int j:firstRowX)
            j += mt*addX[type];
        return new int[][]{firstRowX, firstRowY};
    }

}

class backYard{
    private generateMap matrix;
    private ArrayList<LawnMower> lawnMowers = new ArrayList<LawnMower>();
    private Background backYardImg;
    private ArrayList<Icons> availableIcon = new ArrayList<Icons>();
    private int level;
    private long lastAdded = 0;
    private Pane backYard;
    private int sunCount = 150;
    protected Timeline setUpTime;
    protected boolean pausePlay = false;
    Stage ps;
    Scene hp;
    public backYard(int level, Scene x, Stage y){
        try{
            ps = y;
            hp = x;
            backYard = new Pane();
            this.level = level;
            String[] address;
            if (level<=2){
                backYardImg = new Background(new BackgroundImage(new Image(new FileInputStream("PlantsVsZombies_Images/Level1lawn.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1366,768,false,false,false,false)));
                lawnMowers.add(new LawnMower(new int[]{240,375}));
                matrix = new generateMap(1);
                if (level == 1)
                    address = new String[]{"PeashooterSeed.png"};
                else
                    address = new String[]{"PeashooterSeed.png","SunflowerSeed.png"};
            }else if (level == 3) {
                backYardImg = new Background(new BackgroundImage(new Image(new FileInputStream("PlantsVsZombies_Images/Level3lawn.png")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1366, 768, false, false, false, false)));
                for(int i = 0;i<3;i++)
                    lawnMowers.add(new LawnMower(new int[]{240,260+i*130}));
                matrix = new generateMap(3);
                address = new String[]{"PeashooterSeed.png","SunflowerSeed.png","CherryBombSeed.png"};
            }else{
                backYardImg = new Background(new BackgroundImage(new Image(new FileInputStream("PlantsVsZombies_Images/Lawn.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1366,768,false,false,false,false)));
                for(int i = 1;i<6;i++)
                    lawnMowers.add(new LawnMower(new int[]{240,i*130}));
                matrix = new generateMap(5);
                if (level == 4)
                    address = new String[]{"PeashooterSeed.png","SunflowerSeed.png","CherryBombSeed.png","WallnutSeed.png"};
                else
                    address = new String[]{"PeashooterSeed.png","SunflowerSeed.png","CherryBombSeed.png","WallnutSeed.png","PotatoMineSeed.png"};
            }
            for(String el:address)
                availableIcon.add(new Icons(el,new int[]{75,77}));
            initBackyard();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public Pane getBackYard(){
        return backYard;
    }

    public void play(){
        this.setupTimeLine();
    }

    public void setupTimeLine(){
        KeyFrame newKey = new KeyFrame(Duration.seconds(1), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }


    public Pane initBackyard() {
        try {
            backYard.setBackground(backYardImg);
            ImageView timer = new ImageView(new Image(new FileInputStream("PlantsVsZombies_Images/Timer.png"), 240, 30, false, false));
            ImageView zombieIcon = new ImageView(new Image(new FileInputStream("PlantsVsZombies_Images/zombieTimer.png"), 40, 40, false, false));
            setLayout(timer,1000,740);
            setLayout(zombieIcon,1200,730);
            backYard.getChildren().addAll(timer,zombieIcon);
            ImageView iconBar = new ImageView(new Image(new FileInputStream("PlantsVsZombies_Images/iconBar.png"), 600, 100, false, false));
            Label sunCountLabel = new Label();
            sunCountLabel.setText(sunCount + "");
            sunCountLabel.getStyleClass().addAll("textField_color", "textField_color_black");
            setLayout(sunCountLabel, 48, 65);
            setLayout(iconBar, 10, 0);
            InGameMenu menuButton = new InGameMenu(this);
            backYard.getChildren().addAll(iconBar, sunCountLabel);
            Plants peashootEg = new PeaShooter(new int[]{335,340}, this);
            for (LawnMower elements : lawnMowers)
                backYard.getChildren().add(elements.getLawnMowerImg());
            int j = 0;
            for (Icons i : availableIcon) {
                backYard.getChildren().add(i.getIconImage(j));
                j++;
                backYard.getStylesheets().add("/sample/CSSButtonShape.CSS");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return backYard;
    }

    public Object setLayout(Object o, int x, int y){
        Node nd = (Node)o;
        nd.setLayoutX(x);
        nd.setLayoutY(y);
        return (Object)nd;
    }

    public void sunFallDrop(){
        Sun s = new Sun();
        backYard.getChildren().add(s.sunImage);
    }

    public void act() {
        long curTime = System.currentTimeMillis();
        if(curTime>=lastAdded + 20000){
            lastAdded = curTime;
            sunFallDrop();
            Zombies z = new NormalZombie(300,this);
            backYard.getChildren().add(z.actorImage);
        }
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            act();
        }
    }
}

class Icons{
    private ImageView iconImage;

    public Icons(String address,int size[]) throws FileNotFoundException{
        iconImage = new ImageView(new Image(new FileInputStream("PlantsVsZombies_Images/"+address),size[0],size[1],false,false));
    }

    public ImageView getIconImage(int x){
        iconImage.setLayoutX(120+80*x);
        iconImage.setLayoutY(10);
        return iconImage;
    }
}

class InGameMenu{
    private backYard lawn;
    private Button menuButton = new Button();
    protected Timeline setUpTime;
    Button backToMenu = new Button();
    ImageView pauseMenu;

    public InGameMenu(backYard lawn){
        this.lawn = lawn;
        try{
        menuButton.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("PlantsVsZombies_Images/InGameMenuButton.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT,new BackgroundSize(131,41,false,false,false,false))));
        backToMenu.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("PlantsVsZombies_Images/mainMenuButton.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT,new BackgroundSize(225,38,false,false,false,false))));
        setLayout(backToMenu,612,362);
        backToMenu.setMinSize(215,38);
        setLayout(menuButton,1200,0);
        menuButton.setMinSize(131,41);
        this.lawn.getBackYard().getChildren().add(menuButton);
        setupTimeLine();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void setLayout(Node e,int x,int y){
        e.setLayoutX(x);
        e.setLayoutY(y);
    }

    public void setupTimeLine(){
        KeyFrame newKey = new KeyFrame(Duration.millis(1), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            menuButton.setOnMousePressed(e->{
                try{
                    menuButton.setDisable(true);
                    lawn.setUpTime.pause();
                    lawn.pausePlay = true;
                    pauseMenu = new ImageView(new Image(new FileInputStream("PlantsVsZombies_Images/InGameMenu.png"),435,506,false,false));
                    setLayout(pauseMenu, 500,100);
                    lawn.getBackYard().getChildren().addAll(pauseMenu,backToMenu);
                }catch (FileNotFoundException f){
                    System.out.println(f.getMessage());
                }
            });
            backToMenu.setOnMousePressed(e->{
                lawn.ps.setScene(lawn.hp);
                lawn.getBackYard().getChildren().removeAll(backToMenu,pauseMenu);
                lawn.pausePlay = false;
                menuButton.setDisable(false);
            });
        }
    }
}

class LawnMower{
    private boolean active = false;
    private ImageView lawnMowerImg;
    private int sunCount = 0;

    public LawnMower(int[] setPos) throws FileNotFoundException {
        lawnMowerImg = new ImageView(new Image(new FileInputStream("PlantsVsZombies_Images/Lawnmower.png"),125,90,false,false));
        lawnMowerImg.setLayoutX(setPos[0]);
        lawnMowerImg.setLayoutY(setPos[1]);
    }

    public ImageView getLawnMowerImg() {
        return lawnMowerImg;
    }
}

class Sun{
    protected ImageView sunImage;
    private int speed = 4;
    private int pos[] = {0,0};

    public Sun() {
        try{
        sunImage = new ImageView(new Image(new FileInputStream("PlantsVsZombies_Images/sun.gif"),50,60,false,false));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        move();
    }

    public void move(){
        Random path = new Random();
        TranslateTransition sunDrop = new TranslateTransition();
        sunDrop.setDuration(Duration.millis(speed*1000));
        this.pos[0] = path.nextInt(900) + 350;
        this.pos[1] = path.nextInt(550) + 130;
        sunImage.setLayoutY(0);
        sunImage.setLayoutX(pos[0]);
        sunDrop.setNode(sunImage);
        sunDrop.setCycleCount(1);
        sunDrop.setByY(pos[1]);
        sunDrop.play();
    }
}

public class Main extends Application {
    private String UserName;
    private backYard mainLawn;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Pane HomePage = new Pane();
        Scene hp = new Scene(HomePage,1366,768);
        mainLawn = new backYard(1, hp, primaryStage);
        Pane NewUserPane = new Pane();
        Scene mainLawnScene = new Scene(mainLawn.getBackYard(),1366,768);
        Scene NewUser = new Scene(NewUserPane,1366,768);
        primaryStage.setTitle("Plants Vs Zombies");
        Pane LoadingPane = new Pane();
        Image main = new Image(new FileInputStream("PlantsVsZombies_Images/MainBack.jpg"));
        BackgroundImage bgMain = new BackgroundImage(main, BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,new BackgroundSize(1376,768.,false,false,false,false));
        LoadingPane.setBackground(new Background(bgMain));

        //Creating Loading Page
        Button b = new Button();
        b.setLayoutX(480);
        b.setLayoutY(678);
        b.setMinSize(410,63);
        b.setOpacity(0);
        BackgroundImage bLoad = new BackgroundImage(new Image(new FileInputStream("PlantsVsZombies_Images/LoadGame.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(418,58,false,false,false,false));
        b.setBackground(new Background(bLoad));
        LoadingPane.getChildren().add(b);
        Image hmpage = new Image(new FileInputStream("PlantsVsZombies_Images/HomePage.png"));
        BackgroundImage bgHm = new BackgroundImage(hmpage,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1376,768,false,false,false,false));
        HomePage.setBackground(new Background(bgHm));
        Button adventureB = new Button();
        BackgroundImage aventb = new BackgroundImage(new Image(new FileInputStream("PlantsVsZombies_Images/AdventureMode.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(555,155,false,false,false,false));
        BackgroundImage aventb1 = new BackgroundImage(new Image(new FileInputStream("PlantsVsZombies_Images/AdventureMode2.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(555,155,false,false,false,false));
        BackgroundImage aventb2 = new BackgroundImage(new Image(new FileInputStream("PlantsVsZombies_Images/AdventureMode3.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(555,155,false,false,false,false));
        adventureB.setBackground(new Background(aventb));
        adventureB.setLayoutY(100);
        adventureB.setLayoutX(700);
        b.setOnMouseEntered(e->{
            b.setOpacity(100);

        });
        b.setOnMouseExited(e->{
            b.setOpacity(0);
        });
        b.setOnMouseClicked(e->{
            primaryStage.setScene(NewUser);
        });

        //Adding New User
        BackgroundImage NewUserBg = new BackgroundImage(new Image(new FileInputStream("PlantsVsZombies_Images/NewUserbg.jpg")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1366,768,false,false,false,false));
        NewUserPane.setBackground(new Background(NewUserBg));
        ImageView img = new ImageView(new Image(new FileInputStream("PlantsVsZombies_Images/New_User.png")));
        Button UserOk = new Button();
        Image okbi = new Image(new FileInputStream("PlantsVsZombies_Images/NewUserok.png"));
        Image okb2 = new Image(new FileInputStream(("PlantsVsZombies_Images/NewUserok2.png")));
        Image okb3 = new Image(new FileInputStream(("PlantsVsZombies_Images/NewUserok3.png")));
        Background Userok1 =new Background(new BackgroundImage(okbi,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,BackgroundSize.DEFAULT));
        Background Userok2 =new Background(new BackgroundImage(okb2,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,BackgroundSize.DEFAULT));
        Background Userok3 =new Background(new BackgroundImage(okb3,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,BackgroundSize.DEFAULT));
        UserOk.setBackground(Userok1);
        UserOk.setMinSize(216,47);
        UserOk.setLayoutX(605);
        UserOk.setLayoutY(390);
        img.setLayoutX(400);
        img.setLayoutY(100);
        TextField UserNameTf = new TextField();
        UserNameTf.setMinSize(438,60);
        UserNameTf.setLayoutX(492);
        UserNameTf.setLayoutY(288);
        UserNameTf.setAlignment(Pos.CENTER);
        UserNameTf.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue())
                    if (UserNameTf.getText().length() >= 26)
                        UserNameTf.setText(UserNameTf.getText().substring(0, 26));
            }
        });
        UserNameTf.getStyleClass().add("textField_color");
        NewUserPane.getChildren().add(img);
        NewUserPane.getChildren().add(UserNameTf);
        NewUserPane.getChildren().add(UserOk);
        NewUserPane.getStylesheets().add("/sample/CSSButtonShape.CSS");

        //Main HomePage\
        BackgroundImage miniB1 = new BackgroundImage(new Image(new FileInputStream("PlantsVsZombies_Images/miniGames1.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(537,120,false,false,false,false));
        BackgroundImage miniB2 = new BackgroundImage(new Image(new FileInputStream("PlantsVsZombies_Images/miniGames2.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(537,120,false,false,false,false));
        BackgroundImage miniB3 = new BackgroundImage(new Image(new FileInputStream("PlantsVsZombies_Images/miniGames3.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(537,120,false,false,false,false));
        Button miniGameB = new Button();
        miniGameB.setMinSize(540,120);
        miniGameB.setRotate(6);
        miniGameB.setLayoutY(248);
        miniGameB.setLayoutX(686);
        miniGameB.setBackground(new Background(miniB1));
        Label UserLabel = new Label();
        UserLabel.setLayoutX(94);
        UserLabel.setLayoutY(90); //cant add more than 28 characters
        UserLabel.setMinSize(400,60);
        UserLabel.setMaxSize(600,60);
        UserLabel.getStyleClass().add("text_UserName");
        Label LevelTf = new Label();
        LevelTf.setText("New Game");
        LevelTf.setLayoutX(871);
        LevelTf.setLayoutY(205);
        LevelTf.setMinSize(140,40);
        LevelTf.setMaxSize(140,40);
        LevelTf.setRotate(5);
        LevelTf.getStyleClass().add("textField_colorLevel");
        LevelTf.setAlignment(Pos.CENTER);

        HomePage.getChildren().add(miniGameB);
        HomePage.getChildren().add(UserLabel);
        adventureB.setMinSize(555,155);
        adventureB.setRotate(5);
        HomePage.getChildren().add(adventureB);
        HomePage.getChildren().add(LevelTf);
        HomePage.getStylesheets().add("/sample/CSSButtonShape.CSS");

        LevelTf.setOnMouseClicked(e->{
            if (LevelTf.getText().equals("New Game")){
                LevelTf.setText("Choose Level");
            }else{
                LevelTf.setText("New Game");
            }
        });
        UserOk.setOnMouseEntered(e->{
            UserOk.setBackground(Userok2);
        });
        UserOk.setOnMouseExited(e->{
            UserOk.setBackground(Userok1);
        });
        UserOk.setOnAction(e->{
            UserName = UserNameTf.getText();
            System.out.println(UserName);
            if(!UserName.equals("")) {
                primaryStage.setScene(hp);
            }
            UserLabel.setText(UserName);
            UserLabel.setAlignment(Pos.CENTER);
        });
        UserOk.setOnMousePressed(e->{
            UserOk.setBackground(Userok3);
        });
        UserOk.setOnMouseReleased(e->{
            UserOk.setBackground(Userok1);
        });
        adventureB.setOnMouseEntered(e->{
            adventureB.setBackground(new Background(aventb1));
        });
        adventureB.setOnMouseExited(e->{
            adventureB.setBackground(new Background(aventb));
        });
        adventureB.setOnMousePressed(e->{
            adventureB.setBackground(new Background(aventb2));
            if (LevelTf.getText().equals("New Game")){
                primaryStage.setScene(mainLawnScene);
                mainLawn.play();
            }
        });
        adventureB.setOnMouseReleased(e->{
            adventureB.setBackground(new Background(aventb1));
        });
        miniGameB.setOnMouseEntered(e->{
            miniGameB.setBackground(new Background(miniB2));
        });
        miniGameB.setOnMouseExited(e->{
            miniGameB.setBackground(new Background(miniB1));
        });
        miniGameB.setOnMousePressed(e->{
            miniGameB.setBackground(new Background(miniB3));
        });
        miniGameB.setOnMouseReleased(e->{
            miniGameB.setBackground(new Background(miniB2));
        });

        primaryStage.setScene(new Scene(LoadingPane,1366,768));
        primaryStage.setResizable(false);
        primaryStage.show();
        mainLawn.act();
    }


    public static void main(String[] args) {
        launch(args);
    }
}