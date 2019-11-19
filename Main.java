package sample;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.*;

interface cloneable{}

abstract class Actor{
    protected double Health;
    protected double Attackpow;
    protected double[] pos = new double[2];
    protected String[] Category = new String[2];
    protected ImageView actorImage;
    protected Timeline setUpTime;
    protected double speed = 1000;
    abstract public void act();

    public double getHealth() {
        return Health;
    }

    public double getAttackpow() {
        return Attackpow;
    }

    public double[] getPos() {
        return pos;
    }

    public Timeline getSetUpTime() {
        return setUpTime;
    }
}

class Plants extends Actor implements cloneable {
    protected long lastActTime = 0;
    protected int key;
    protected double translatePos[];
    protected backYard lawn;

    public Plants(){
        Category[0] = "plants";
    }

    public void addToLawn(double[] pos){
        actorImage.setLayoutX(pos[0]);
        actorImage.setLayoutY(pos[1]);
        lawn.getBackYard().getChildren().add(actorImage);
    }
    public void start(){
        this.pos = new double[]{actorImage.getLayoutX(),actorImage.getLayoutY()};
        lastActTime = System.currentTimeMillis();
        setupTimeLine();
    }

    @Override
    public void act() {}

    public void setupTimeLine(){
        KeyFrame newKey = new KeyFrame(Duration.millis(speed), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            act();
        }
    }

    @Override
    public Plants clone(){
        return new Plants();
    }

    public void setKey(int k){
        key = k;
    }
}

class PeaShooter extends Plants {
    private ArrayList<bullet> b = new ArrayList<bullet>(2);

    public PeaShooter( backYard x) throws FileNotFoundException {
        Health = 300;
        Attackpow = 20;
        Category[1] = "peashooter";
        actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/peaShooter.gif"),120,120,false,false));
        lawn = x;
        translatePos = new double[]{-10,-10};
    }

    @Override
    public void act(){
        long time = System.currentTimeMillis();
        if(!lawn.pausePlay && time>lastActTime+5000){
            bullet newBullet = new bullet(pos);
            if(b.size() == 0 || b.get(0).bulletImage.getX()>1365)
                b.add(0,newBullet);
            else if(b.size() == 1 || b.get(1).bulletImage.getX()>1365)
                b.add(1,newBullet);
            if(b.contains(newBullet)){
                lawn.getBackYard().getChildren().add(newBullet.bulletImage);
                lastActTime = time;
            }
        }
        for (bullet n:b)
            if(lawn.pausePlay)
                n.setUpTime.pause();
            else
                n.setUpTime.play();
    }

    @Override
    public PeaShooter clone(){
        PeaShooter copy = null;
        try {
            copy = new PeaShooter(lawn);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return copy;
    }
}

class bullet{
    ImageView bulletImage;
    private double speed = 0.003;
    Timeline setUpTime;

    public bullet(double[] pos){
        try{
            bulletImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/pea.png"),24,20,false,false));
            bulletImage.setLayoutX(pos[0]+81);
            bulletImage.setLayoutY(pos[1]+40);
            moveBullet();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void moveBullet(){
        KeyFrame newKey = new KeyFrame(Duration.millis(speed*1000), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            bulletImage.setX(bulletImage.getX()+1);
        }
    }
}

class CherryBomb extends Plants{
    protected int x = 1;

    public CherryBomb(backYard x){
        try{
        Category[1] = "cherrybomb";
        Health = 300;
        Attackpow = 1800;
        actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/cherryBomb.png"),118,99,false,false));
        lawn = x;
        this.speed = 45;
        translatePos = new double[]{-10,20};
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void act(){
        long time = System.currentTimeMillis();
        if(time-lastActTime>45)
            try{
                if(x >= 30){
                    lawn.mapOfPlants.get(key).remove(this);
                    lawn.getBackYard().getChildren().remove(this.actorImage);
                    return;
                }
                int size1 = 120+3*x;
                int size2 = 100+3*x;
                actorImage.setImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/CherryBomb ("+x+").gif"),size1,size2,false,false));
                actorImage.setLayoutY(actorImage.getLayoutY()-1);
                actorImage.setLayoutX(actorImage.getLayoutX()-1);
                x++;
                lastActTime = time;
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
    }
    @Override
    public CherryBomb clone(){
        CherryBomb copy = null;
        copy = new CherryBomb(lawn);
        return copy;
    }
}

class SunFlower extends Plants{
    public SunFlower(backYard x){
        try{
            Health = 300;
            Category[1] = "sunFlower";
            System.out.println("hhh");
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/SunFlower.gif"),70,90,false,false));
            lawn = x;
            translatePos = new double[]{10,20};
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    @Override
    public void act() {
        long time = System.currentTimeMillis();
        if(time - lastActTime > 9000){
            Sun s = new Sun(lawn,new double[]{actorImage.getLayoutX()+40,actorImage.getLayoutY()+25},true);
            lawn.getBackYard().getChildren().add(s.sunImage);
            lastActTime = time;
        }
    }

    @Override
    public SunFlower clone(){
        SunFlower copy = null;
        copy = new SunFlower(lawn);
        return copy;
    }
}

class WallNut extends Plants{
    public WallNut(backYard x){
        try{
            Category[1] = "Wallnut";
            Health = 4000;
            Attackpow = 0;
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/walnutFull.gif"),70,90,false,false));
            lawn = x;
            translatePos = new double[]{10,20};
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public WallNut clone(){
        WallNut copy = null;
        copy = new WallNut(lawn);
        return copy;
    }
}

class PotatoMine extends Plants{
    public PotatoMine(backYard x){}
}

class Zombies extends Actor{
    protected Timeline setUpTime;
    protected double speed;
    protected backYard lawn;
    protected LawnMower rowLawnMower;

    public Zombies(int posy,int key,backYard l){
        Category[0] = "Zombies";
        lawn = l;
        this.pos = new double[]{1300,posy};
        rowLawnMower = lawn.getLawnMowers().get(key-1);
    }

    public void setLayout(Node e,double x,double y){
        e.setLayoutY(y);
        e.setLayoutX(x);
    }

    @Override
    public void act() {
        if(this.pos[0] < 295 && !rowLawnMower.isActive()){
            rowLawnMower.setActive(true);
        }
    }

    protected void setupTimeLine(double speed){
        KeyFrame newKey = new KeyFrame(Duration.millis(1000*speed), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            if(!lawn.pausePlay)
                act();
        }
    }
}

class NormalZombie extends Zombies{

    public NormalZombie(int posy, int key, backYard x){
        super(posy,key,x);
        try{
            Category[1] = "NormalZombie";
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/normalZombieWalking.gif"),61,150,false,false));
            speed = 0.05;
            setLayout(actorImage,pos[0],pos[1]);
            setupTimeLine(speed);
        } catch (FileNotFoundException f) {
            System.out.println(f.getMessage());
        }
    }

    @Override
    public void act(){
        super.act();
        actorImage.setLayoutX(actorImage.getLayoutX()-1);
        this.pos[0] = actorImage.getLayoutX();
    }
}

class FlagZombie extends Zombies{
    public FlagZombie(int posy,int key,backYard x) {
        super(posy,key,x);
        try {
            Category[1] = "FlagZombie";
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/normalZombieWalking.gif"),81,130,false,false));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class ConeHeadZombie extends Zombies{
    public ConeHeadZombie(int posy,int key,backYard x){
        super(posy,key,x);
        try {
            Category[2] = "ConeHeadZombie";
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/coneheadZombieWalking.gif"),81,130,false,false));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class FootballZombie extends Zombies{
    public FootballZombie(int posy,int key, backYard x){
        super(posy,key,x);
        try {
            Category[3] = "FootballZombie";
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/footballZombieWalking.gif"),81,130,false,false));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class generateMap{
    private int[] firstRowX;
    private int[] firstColY;
    private int[] key;
    private int nosOfPatches;
    public generateMap(int nosOfPatches){
        this.nosOfPatches = nosOfPatches;
        if (nosOfPatches == 1){
            firstColY = new int[]{345,475};
            key =new int[]{3};
        }
        else if(nosOfPatches == 3){
            firstColY = new int[]{225,345,475,600};
            key = new int[]{2,3,4};
        }
        else{
            firstColY = new int[]{98,225,345,475,600,725};
            key = new int[]{1,2,3,4,5};
        }
        firstRowX = new int[]{345,445,557,657,772,887,988,1090,1200,1332};
    }

    public int[] getKey() {
        return key;
    }

    public int[] getFirstColY() {
        return firstColY;
    }

    public int[] findPos(double x, double y, double[] transPose){
        int posx = (int)transPose[0];
        int posy = (int)transPose[1];
        for (int i = 0; i<9;i++)
            if(x>=firstRowX[i] && x<=firstRowX[i+1])
                posx += firstRowX[i];
        for (int i = 0; i<nosOfPatches ; i++)
            if(y>=firstColY[i] && y<=firstColY[i+1])
                posy += firstColY[i];
        if (posx == transPose[0] || posy == transPose[1])
            return new int[]{0,0};
        return new int[]{posx,posy};
    }
}

class backYard{
    private generateMap matrix;

    private ArrayList<LawnMower> lawnMowers = new ArrayList<LawnMower>();
    private Background backYardImg;
    private ArrayList<Icons> availableIcon = new ArrayList<Icons>();
    private int level;
    private Label sunCountLabel = new Label();
    private long lastAdded = 0;
    private Pane backYard;
    private int sunCount = 150;
    protected Timeline setUpTime;
    protected boolean pausePlay = false;
    HashMap<Integer,ArrayList<Plants>> mapOfPlants = new HashMap<Integer, ArrayList<Plants>>();
    HashMap<Integer,ArrayList<Zombies>> mapOfZombies = new HashMap<>();
    private int nosOfWavesGenerated = 0;
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
                backYardImg = new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Level1lawn.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1366,768,false,false,false,false)));
                lawnMowers.add(new LawnMower(new int[]{240,375},2,this));
                matrix = new generateMap(1);
                if (level == 1)
                    address = new String[]{"PeashooterSeed.png"};
                else
                    address = new String[]{"PeashooterSeed.png","SunflowerSeed.png"};
            }else if (level == 3) {
                backYardImg = new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Level3lawn.png")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1366, 768, false, false, false, false)));
                for(int i = 0;i<3;i++)
                    lawnMowers.add(new LawnMower(new int[]{240,260+i*130},i+2,this));
                matrix = new generateMap(3);
                address = new String[]{"PeashooterSeed.png","SunflowerSeed.png","CherryBombSeed.png"};
            }else{
                backYardImg = new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Lawn.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1366,768,false,false,false,false)));
                for(int i = 1;i<6;i++)
                    lawnMowers.add(new LawnMower(new int[]{240,i*130},i-1,this));
                matrix = new generateMap(5);
                if (level == 4)
                    address = new String[]{"PeashooterSeed.png","SunflowerSeed.png","CherryBombSeed.png","WallnutSeed.png"};
                else
                    address = new String[]{"PeashooterSeed.png","SunflowerSeed.png","CherryBombSeed.png","WallnutSeed.png","PotatoMineSeed.png"};
            }
            for(String el:address)
                availableIcon.add(new Icons(el,new int[]{75,77},this));
            initBackyard();
            sunCountLabel.getStyleClass().addAll("textField_color", "textField_color_black");
            setLayout(sunCountLabel, 48, 65);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<LawnMower> getLawnMowers() {
        return lawnMowers;
    }

    public Pane getBackYard(){
        return backYard;
    }

    public void play(){
        this.setupTimeLine();
    }

    public void GenrateWave(){
        Random zombieGenerator = new Random();
        int nosOfZombies;
        if(nosOfWavesGenerated == 0)
            nosOfZombies = 1+2*level;
        else if(nosOfWavesGenerated == 1)
            nosOfZombies = 2+2*level;
        else
            nosOfZombies = 4+2*level;
        nosOfWavesGenerated++;
        for(int i = 0;i<nosOfZombies;i++){
            Zombies newZ;
            int key = 0;
            if(level <= 2)
                newZ = new NormalZombie(matrix.getFirstColY()[0],matrix.getKey()[key],this);
            else if(level == 3){
                int zc = zombieGenerator.nextInt(2);
                key = zombieGenerator.nextInt(3);
                if(zc == 0)
                    newZ = new NormalZombie(matrix.getFirstColY()[key],matrix.getKey()[key],this);
                else
                    newZ = new ConeHeadZombie(matrix.getFirstColY()[key],matrix.getKey()[key],this);
            }else{
                key = zombieGenerator.nextInt(5);
                int zc = zombieGenerator.nextInt(4);
                if(zc == 0)
                    newZ = new NormalZombie(matrix.getFirstColY()[key],matrix.getKey()[key],this);
                else if(zc<=2)
                    newZ = new ConeHeadZombie(matrix.getFirstColY()[key],matrix.getKey()[key],this);
                else
                    newZ = new FootballZombie(matrix.getKey()[key],matrix.getKey()[key],this);
            }
            ArrayList<Zombies> z;
            if(!mapOfZombies.containsKey(key))
                z = new ArrayList<Zombies>();
            else
                z = mapOfZombies.get(key);
            z.add(newZ);
            mapOfZombies.put(matrix.getKey()[key],z);
        }
    }

    public Pane initBackyard() {
        try {
            backYard.setBackground(backYardImg);
            ImageView timer = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Timer.png"), 240, 30, false, false));
            ImageView zombieIcon = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/zombieTimer.png"), 40, 40, false, false));
            setLayout(timer,1000,740);
            setLayout(zombieIcon,1200,730);
            backYard.getChildren().addAll(timer,zombieIcon);
            ImageView iconBar = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/iconBar.png"), 600, 100, false, false));
            setLayout(sunCountLabel, 48, 65);
            setLayout(iconBar, 10, 0);
            InGameMenu menuButton = new InGameMenu(this);
            backYard.getChildren().addAll(iconBar, sunCountLabel);
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
        Random path = new Random();
        double[] pos = {path.nextInt(900) + 350,path.nextInt(550) + 130};
        Sun s = new Sun(this,pos,false);
        backYard.getChildren().add(s.sunImage);
    }

    public int getSunCount() {
        return sunCount;
    }

    public int getLevel() {
        return level;
    }

    public generateMap getMatrix() {
        return matrix;
    }

    public void act() {
        long curTime = System.currentTimeMillis();
        sunCountLabel.setText(sunCount+"");
        if(curTime>=lastAdded + 20000){
            lastAdded = curTime;
            sunFallDrop();
        }
    }

    public void setSunCount(int sunCount) {
        this.sunCount += sunCount;
    }

    public void setupTimeLine(){
        KeyFrame newKey = new KeyFrame(Duration.seconds(1), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            act();
        }
    }
}

class Icons extends Button{
    protected Timeline setUpTime;
    private backYard lawn;
    private String IconType;
    private Plants type;
    private Image iconImage;
    private generateMap map;
    private Plants copy;
    private double translatePos[];

    public Icons(String address,int size[], backYard x) throws FileNotFoundException{
        lawn = x;
        map = lawn.getMatrix();
        if (address.equals("PeashooterSeed.png")){
            IconType = "peaShooter.gif";
            type = new PeaShooter(x);
            translatePos = type.translatePos;
        }
        else if(address.equals("WallnutSeed.png")){
            IconType = "Wallnut.png";
            type = new WallNut(x);
            translatePos = type.translatePos;
        }
        else if(address.equals("SunflowerSeed.png")){
            IconType = "SunFlower.gif";
            type = new SunFlower(x);
            translatePos = type.translatePos;
        }
        else if(address.equals("PotatoMineSeed.png")) {
            IconType = "PotatoMine.gif";
            type = new PotatoMine(x);
            translatePos = type.translatePos;
        }else{
            IconType = "CherryBomb.gif";
            type = new CherryBomb(x);
            translatePos = type.translatePos;
        }
        iconImage = new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/"+address));
        this.setMinSize(size[0],size[1]);
        this.setBackground(new Background(new BackgroundImage(iconImage,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(size[0],size[1],false,false,false,false))));
        this.setOnMouseEntered(e->{
            Light.Spot light = new Light.Spot();
            light.setX(this.getLayoutX()+40);
            light.setY(this.getLayoutY()+30);
            light.setZ(100);
            Lighting actlight = new Lighting();
            this.setEffect(actlight);
        });
        this.setOnMouseExited(e->{
            Glow g = new Glow();
            g.setLevel(0);
            this.setEffect(g);
        });
        this.setOnMousePressed(e->{
            try{
                e.setDragDetect(true);
                Plants copy = type.clone();
                System.out.println(e.getSceneX()+","+e.getSceneY());
                copy.addToLawn(new double[]{e.getSceneX()-30,e.getSceneY()-20});
                this.copy = copy;

            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        });

        this.setOnMouseDragged(e->{
            e.setDragDetect(false);
            copy.actorImage.setLayoutY(e.getSceneY()-20);
            copy.actorImage.setLayoutX(e.getSceneX()-30);
        });

        this.setOnMouseReleased(e->{
            e.setDragDetect(false);
            int[] pos = map.findPos(e.getSceneX(),e.getSceneY(),translatePos);
            boolean add = pos[0] != 0;
            copy.actorImage.setLayoutY(pos[1]);
            copy.actorImage.setLayoutX(pos[0]);
            int key = (pos[1]-15)/100;
            if (key == 0)
                key = 1;
            ArrayList<Plants> addPlants;
            if (lawn.mapOfPlants.containsKey(key))
                addPlants = lawn.mapOfPlants.get(key);
            else
                addPlants = new ArrayList<Plants>();
            for(Plants existing:addPlants){
                if(pos[0]-translatePos[0] == existing.pos[0] - existing.translatePos[0]){
                    add = false;
                    break;
                }
            }
            if(add){
                addPlants.add(copy);
                lawn.mapOfPlants.put(key,addPlants);
                copy.setKey(key);
                System.out.println(key);
                copy.start();
            }else{
                lawn.getBackYard().getChildren().remove(copy.actorImage);
            }
        });
    }

    public Icons getIconImage(int x){
        this.setLayoutX(120+80*x);
        this.setLayoutY(10);
        return this;
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
            menuButton.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/InGameMenuButton.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT,new BackgroundSize(131,41,false,false,false,false))));
            backToMenu.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/mainMenuButton.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT,new BackgroundSize(225,38,false,false,false,false))));
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
                    pauseMenu = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/InGameMenu.png"),435,506,false,false));
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
    private backYard lawn;
    private int Row;
    Timeline setUpTime;

    public LawnMower(int[] setPos, int Row, backYard lawn) throws FileNotFoundException {
        lawnMowerImg = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Lawnmower.png"),125,90,false,false));
        lawnMowerImg.setLayoutX(setPos[0]);
        lawnMowerImg.setLayoutY(setPos[1]);
        this.Row = Row;
        this.lawn = lawn;
        setupTimeLine();
    }
    public ImageView getLawnMowerImg() {
        return lawnMowerImg;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setupTimeLine(){
        KeyFrame newKey = new KeyFrame(Duration.millis(1000*0.01), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            if(active)
                lawnMowerImg.setX(lawnMowerImg.getX()+1);
            if(lawnMowerImg.getX()>1366)
                setUpTime.stop();
        }
    }
}

class Sun{
    protected ImageView sunImage;
    private double speed = 0.01;
    private double pos[] = {0,0};
    Timeline setUpTime;
    private backYard lawn;
    private boolean sunFlowerProduce = false;

    public Sun(backYard lawn, double[] pos,boolean value) {
        try{
            this.lawn = lawn;
            sunImage = new ImageView(new Image(new FileInputStream( "src/sample/PlantsVsZombies_Images/sun.gif"),45,45,false,false));
            this.pos = pos;
            this.sunFlowerProduce = value;
            sunImage.setLayoutX(this.pos[0]);
            sunImage.setLayoutY(0);
            if (value)
                sunImage.setLayoutY(this.pos[1]-20);
            setupTimeLine();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void setupTimeLine() {
        KeyFrame newKey = new KeyFrame(Duration.millis(speed*1000), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        private boolean mousePressed = false;
        public void handle(ActionEvent event){
            if (sunImage.getLayoutY() < pos[1]+10 && !mousePressed) {
                sunImage.setLayoutY(sunImage.getLayoutY()+1);
            }
            sunImage.setOnMousePressed(e->{
                TranslateTransition moveDiagnol;
                mousePressed = true;
                moveDiagnol = new TranslateTransition();
                moveDiagnol.setDuration(Duration.millis(1000));
                moveDiagnol.setNode(sunImage);
                moveDiagnol.setToX(40-sunImage.getLayoutX());
                moveDiagnol.setByY(20-sunImage.getLayoutY());
                moveDiagnol.setCycleCount(1);
                moveDiagnol.play();
                lawn.setSunCount(25);
                moveDiagnol.setOnFinished(x->{
                    lawn.getBackYard().getChildren().remove(sunImage);
                });
            });
        }
    }
}

public class Main extends Application {
    private String UserName;
    private backYard mainLawn;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Pane HomePage = new Pane();
        Scene hp = new Scene(HomePage,1366,768);
        mainLawn = new backYard(4, hp, primaryStage);
        Pane NewUserPane = new Pane();
        Scene mainLawnScene = new Scene(mainLawn.getBackYard(),1366,768);
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
        primaryStage.setScene(mainLawnScene);
        mainLawn.play();
        primaryStage.setResizable(false);
        primaryStage.show();
        mainLawn.act();
    }


    public static void main(String[] args) {
        launch(args);
    }
}