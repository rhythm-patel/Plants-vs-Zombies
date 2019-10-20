package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main extends Application {
    private String UserName;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Pane HomePage = new Pane();
        Scene hp = new Scene(HomePage,1366,768);
        Pane NewUserPane = new Pane();
        Scene NewUser = new Scene(NewUserPane,1366,768);
        primaryStage.setTitle("Plants Vs Zombies");
        Pane LoadingPane = new Pane();
        Image main = new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/MainBack.jpg"));
        BackgroundImage bgMain = new BackgroundImage(main, BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,new BackgroundSize(1376,768.,false,false,false,false));
        LoadingPane.setBackground(new Background(bgMain));

        //Creating Loading Page
        Button b = new Button();
        b.setLayoutX(480);
        b.setLayoutY(678);
        b.setMinSize(410,63);
        b.setOpacity(0);
        BackgroundImage bLoad = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/LoadGame.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(418,58,false,false,false,false));
        b.setBackground(new Background(bLoad));
        LoadingPane.getChildren().add(b);
        Image hmpage = new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/HomePage.png"));
        BackgroundImage bgHm = new BackgroundImage(hmpage,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1376,768,false,false,false,false));
        HomePage.setBackground(new Background(bgHm));
        Button adventureB = new Button();
        BackgroundImage aventb = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/AdventureMode.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(555,155,false,false,false,false));
        BackgroundImage aventb1 = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/AdventureMode2.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(555,155,false,false,false,false));
        BackgroundImage aventb2 = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/AdventureMode3.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(555,155,false,false,false,false));
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
        BackgroundImage NewUserBg = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/NewUserbg.jpg")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1366,768,false,false,false,false));
        NewUserPane.setBackground(new Background(NewUserBg));
        ImageView img = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/New_User.png")));
        Button UserOk = new Button();
        Image okbi = new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/NewUserok.png"));
        Image okb2 = new Image(new FileInputStream(("src/sample/PlantsVsZombies_Images/NewUserok2.png")));
        Image okb3 = new Image(new FileInputStream(("src/sample/PlantsVsZombies_Images/NewUserok3.png")));
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
        BackgroundImage miniB1 = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/miniGames1.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(537,120,false,false,false,false));
        BackgroundImage miniB2 = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/miniGames2.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(537,120,false,false,false,false));
        BackgroundImage miniB3 = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/miniGames3.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(537,120,false,false,false,false));
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
        LevelTf.setText("Level 1-1");
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

        //Creating MainLevelScene
        Pane level = new Pane();
        BackgroundImage LawnImage = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Lawn.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1366,768,false,false,false,false));
        ImageView lawnMowerImg = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Lawnmower.png"),125,90,false,false));
        ImageView lawnMowerImg2 = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Lawnmower.png"),125,90,false,false));
        ImageView lawnMowerImg3 = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Lawnmower.png"),125,90,false,false));
        ImageView lawnMowerImg4 = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Lawnmower.png"),125,90,false,false));
        ImageView lawnMowerImg5 = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Lawnmower.png"),125,90,false,false));
        ImageView peashooterSeedImg = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/PeashooterSeed.png"),150,80,false,false));
        ImageView sunflowerSeedImg = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/SunflowerSeed.png"),154,84,false,false));
        ImageView cherryBombImg = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/CherryBombSeed.png"),150,80,false,false));
        ImageView wallnutImg = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/WallnutSeed.png"),156,82,false,false));
        ImageView potatoMineImg = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/PotatoMineSeed.png"),150,80,false,false));


        lawnMowerImg.setLayoutY(130);
        lawnMowerImg.setLayoutX(240);
        lawnMowerImg2.setLayoutY(260);
        lawnMowerImg2.setLayoutX(240);
        lawnMowerImg3.setLayoutY(375);
        lawnMowerImg3.setLayoutX(240);
        lawnMowerImg4.setLayoutY(510);
        lawnMowerImg4.setLayoutX(240);
        lawnMowerImg5.setLayoutY(650);
        lawnMowerImg5.setLayoutX(240);
        peashooterSeedImg.setLayoutY(10);
        peashooterSeedImg.setLayoutX(10);
        sunflowerSeedImg.setLayoutY(100);
        sunflowerSeedImg.setLayoutX(8);
        cherryBombImg.setLayoutY(190);
        cherryBombImg.setLayoutX(11);
        wallnutImg.setLayoutY(280);
        wallnutImg.setLayoutX(6);
        potatoMineImg.setLayoutY(370);
        potatoMineImg.setLayoutX(12);

        level.getChildren().add(lawnMowerImg);
        level.getChildren().add(lawnMowerImg2);
        level.getChildren().add(lawnMowerImg3);
        level.getChildren().add(lawnMowerImg4);
        level.getChildren().add(lawnMowerImg5);
        level.getChildren().add(peashooterSeedImg);
        level.getChildren().add(sunflowerSeedImg);
        level.getChildren().add(cherryBombImg);
        level.getChildren().add(wallnutImg);
        level.getChildren().add(potatoMineImg);

        level.setBackground(new Background(LawnImage));
        primaryStage.setScene(new Scene(LoadingPane,1366,768));
        primaryStage.setScene(new Scene(level,1366,768));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

