package sample;
import com.sun.javafx.geom.ShapePair;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.io.*;
import java.lang.annotation.ElementType;
import java.sql.SQLOutput;
import java.util.*;

abstract class Actor implements Serializable{
    public static final long serialVersionUID = 41L;
    volatile protected double Health;
    protected double Attackpow;
    protected double[] pos = new double[2];
    protected String[] Category = new String[2];
    transient protected ImageView actorImage;
    transient protected Timeline setUpTime;
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

    public void setHealth(double v) {
        this.Health -= v;
    }

    abstract public void attack();
}

class Plants extends Actor implements Cloneable {
    protected long lastActTime = 0;
    protected int key;
    protected double translatePos[];
    protected backYard lawn;
    volatile protected ArrayList<Zombies> zombieAttack = new ArrayList<Zombies>();
    protected int price;
    private boolean Dead = false;

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

    public void loadGame(backYard l) throws IOException{
        lawn = l;
    }

    @Override
    public void act() {
        if(lawn.pausePlay)
            return;
        if(Health<=0){
            Dead = true;
            lawn.getBackYard().getChildren().remove(this.actorImage);
            lawn.mapOfPlants.get(key).remove(this);
            for(Zombies e:zombieAttack){
                e.AttackStart = false;
                e.nextPlant = null;
            }
            this.setUpTime.stop();
        }

    }

    public ArrayList<Zombies> getZombieAttack(){
        return zombieAttack;
    }

    public void setZombieAttack(Zombies x){
        zombieAttack.add(x);
    }
    @Override
    public void attack(){}

    public void setupTimeLine(){
        KeyFrame newKey = new KeyFrame(Duration.millis(speed), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            if(!Dead)
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
        speed = 100;
        Category[1] = "peashooter";
        actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/peaShooter.gif"),90,90,false,false));
        lawn = x;
        price = 100;
        translatePos = new double[]{10,20};
    }

    @Override
    public void loadGame(backYard l) throws IOException{
        super.loadGame(l);
        actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/peaShooter.gif"),90,90,false,false));
        lawn.setLayout(actorImage,pos[0],pos[1]);
        lawn.getBackYard().getChildren().add(actorImage);
        if(b != null)
            for(bullet x: b)
                x.loadGame(lawn);
        setupTimeLine();
    }

    @Override
    public void act(){
        super.act();
        long time = System.currentTimeMillis();
        if(!lawn.pausePlay && time>lastActTime+1800){
            ArrayList<Zombies> z = lawn.mapOfZombies.get(key);
            if(z == null || z.size() == 0)
                return;
            boolean noShooting = true;
            for(Zombies x:z)
                if(x.getPosX()>pos[0]){
                    noShooting = false;
                    break;
                }
            if(noShooting)
                return;
            bullet newBullet = new bullet(pos,lawn,key);
            if(b.size() == 0 || b.get(0).getX()>1365)
                b.add(0,newBullet);
            else if(b.size() == 1 || b.get(1).getX()>1365)
                b.add(1,newBullet);
            if(b.contains(newBullet)){
                lawn.getBackYard().getChildren().add(newBullet.bulletImage);
                lastActTime = time;
            }
        }
        for (bullet n:b){
            if(lawn.pausePlay)
                n.setUpTime.pause();
            else
                n.setUpTime.play();
        }
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

class bullet implements Serializable{
    public static final long serialVersionUID = 43L;
    transient ImageView bulletImage;
    private double speed = 0.003;
    private double initPoint;
    transient Timeline setUpTime;
    private double[] pos;
    private backYard lawn;
    private int attackPow = 20;
    private int key;
    private ArrayList<Zombies> rowZombie;

    public bullet(double[] pos, backYard pane, int key){
        try{
            lawn = pane;
            this.key = key;
            rowZombie = lawn.mapOfZombies.get(key);
            bulletImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/pea.png"),24,20,false,false));
            bulletImage.setLayoutX(pos[0]+60);
            initPoint = pos[0]+41;
            bulletImage.setLayoutY(pos[1]+20);
            this.pos = new double[]{pos[0]+60, pos[0]+41};
            moveBullet();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void loadGame(backYard l) throws IOException{
        lawn = l;
        bulletImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/pea.png"),24,20,false,false));
        bulletImage.setLayoutX(pos[0]);
        bulletImage.setLayoutY(pos[1]);
        lawn.getBackYard().getChildren().add(this.bulletImage);
        moveBullet();
    }


    public void attack(){
        Zombies nearestZombie = new Zombies(1,3,lawn,2);
        if(rowZombie != null)
            for(Zombies z:rowZombie){
                if(z.getPosX() < nearestZombie.getPosX() && z.getPosX()>initPoint)
                    nearestZombie = z;
            }
        else
            return;
        if (nearestZombie == null) {
            return;
        }
        if(Math.abs(getX()-nearestZombie.getPosX()) <= 2){
            nearestZombie.setHealth(attackPow);
            lawn.getBackYard().getChildren().remove(this.bulletImage);
            bulletImage.setLayoutX(1367);
            setUpTime.stop();
        }
    }

    public double getX(){
        return this.bulletImage.getLayoutX();
    }
    public void moveBullet(){
        KeyFrame newKey = new KeyFrame(Duration.millis(speed*1000), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            attack();
            bulletImage.setLayoutX(bulletImage.getLayoutX()+1);
            pos[0] = bulletImage.getLayoutX();
            if(getX()>1365){
                setUpTime.stop();
                lawn.getBackYard().getChildren().remove(bulletImage);
            }
        }
    }
}

class CherryBomb extends Plants{
    protected int x = 1;
    private int size1,size2;
    public CherryBomb(backYard x){
        try{
            Category[1] = "cherrybomb";
            Health = 300;
            Attackpow = 1800;
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/cherryBomb.png"),118,99,false,false));
            lawn = x;
            price = 150;
            this.speed = 45;
            translatePos = new double[]{-10,20};
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void loadGame(backYard l) throws IOException{
        super.loadGame(l);
        size1 = 120+3*x;
        size2 = 100+3*x;
        actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/cherryBomb ("+x+").gif"),size1,size2,false,false));
        lawn.setLayout(actorImage,pos[0] - x,pos[1] - x);
        lawn.getBackYard().getChildren().add(actorImage);
        setupTimeLine();
    }

    @Override
    public void act(){
        super.act();
        long time = System.currentTimeMillis();
        if(time-lastActTime>45)
            try{
                if(x >= 30){
                    for(int i = 0;i<3;i++){
                        ArrayList<Zombies> z = lawn.mapOfZombies.containsKey(key-1+i)?lawn.mapOfZombies.get(key-1+i):null;
                        if(z == null)
                            continue;
                        for(Zombies x:z){
                            if(Math.abs(pos[0]-x.getPosX()) <= 200)
                                x.setHealth(x.getHealth());
                        }
                    }
                    lawn.mapOfPlants.get(key).remove(this);
                    lawn.getBackYard().getChildren().remove(this.actorImage);
                    this.setUpTime.stop();
                    return;
                }
                size1 = 120+3*x;
                size2 = 100+3*x;
                actorImage.setImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/CherryBomb ("+x+").gif"),size1,size2,false,false));
                actorImage.setLayoutY(actorImage.getLayoutY()-1);
                actorImage.setLayoutX(actorImage.getLayoutX()-1);
                x++;
                lastActTime = time;
            }catch (Exception e){
                e.printStackTrace();
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
    ArrayList<Sun> sunArrayList;
    public SunFlower(backYard x){
        try{
            Health = 300;
            Category[1] = "sunFlower";
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/SunFlower.gif"),75,100,false,false));
            lawn = x;
            price = 50;
            translatePos = new double[]{10,20};
            sunArrayList = new ArrayList<Sun>();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void loadGame(backYard l) throws IOException{
        super.loadGame(l);
        actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/SunFlower.gif"),75,100,false,false));
        for(Sun s: sunArrayList)
            s.LoadGame();
        lawn.setLayout(actorImage,pos[0],pos[1]);
        lawn.getBackYard().getChildren().add(actorImage);
        setupTimeLine();
    }

    @Override
    public void act() {
        super.act();
        long time = System.currentTimeMillis();
        if(time - lastActTime > 9000 && !lawn.pausePlay){
            Sun s = new Sun(lawn,new double[]{actorImage.getLayoutX()+40,actorImage.getLayoutY()+25},true, sunArrayList);
            sunArrayList.add(s);
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
            price = 50;
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/walnutFull.gif"),70,90,false,false));
            lawn = x;
            translatePos = new double[]{10,20};
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void loadGame(backYard l) throws IOException{
        super.loadGame(l);
        actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/walnutFull.gif"),70,90,false,false));
        lawn.setLayout(actorImage,pos[0],pos[1]);
        lawn.getBackYard().getChildren().add(actorImage);
        setupTimeLine();
    }

    @Override
    public void act(){
        super.act();
        try {
            if (Health <= 2000)
                actorImage.setImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/walnutHalf.gif"), 70, 90, false, false));
        }catch (Exception e){
            e.printStackTrace();
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
    private boolean Activated = false;
    private boolean blastStatus = false;
    private int loadimg = 2;
    private int size[] = new int[2];
    public PotatoMine(backYard x){
        try{
            Category[1] = "potatoMine";
            Health = 300;
            price = 25;
            Attackpow = 1800;
            size[0] = 100;
            size[1] = 120;
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/PotatoMine(2).png"),100,120,false,false));
            lawn = x;
            translatePos = new double[]{5,5};
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void loadGame(backYard l) throws IOException{
        super.loadGame(l);
        actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/PotatoMine("+loadimg+").png"),size[0],size[1],false,false));
        lawn.setLayout(actorImage,pos[0],pos[1]);
        lawn.getBackYard().getChildren().add(actorImage);
        setupTimeLine();
    }

    @Override
    public void start(){
        super.start();
        try{
            actorImage.setImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/PotatoMine(1).png"),90,90,false,false));
            size[0] = 90;
            size[1] = 90;
            loadimg = 1;
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void act(){
        super.act();
        long time = System.currentTimeMillis();
        if(lawn.pausePlay){
            lastActTime = time;
            return;
        }
        try{
            if(time - lastActTime > 14000 && !Activated){
                System.out.println(123);
                actorImage.setImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/PotatoMine(2).png"),100,120,false,false));
                loadimg = 2;
                size[0] = 100;
                size[1] = 120;
                Activated = true;
            }
            if(Activated && !blastStatus){
                ArrayList<Zombies> z = lawn.mapOfZombies.containsKey(key)?lawn.mapOfZombies.get(key):null;
                if(z == null)
                    return;
                for(Zombies x:z)
                    if(Math.abs(pos[0]-x.getPosX()) < 75){
                        x.setHealth(x.getHealth());
                        lastActTime = System.currentTimeMillis();
                        blastStatus = true;
                    }
                if(blastStatus){
                    actorImage.setImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/PotatoMine(3).png"),140,150,false,false));
                    loadimg = 3;
                    size[0] = 140;
                    size[1] = 150;
                }
            }
            if(blastStatus && time - lastActTime>2000){
                lawn.mapOfPlants.get(key).remove(this);
                lawn.getBackYard().getChildren().remove(this.actorImage);
                setUpTime.stop();
            }
            }catch (Exception e){
                e.printStackTrace();
            }
    }

    @Override
    public PotatoMine clone(){
        return new PotatoMine(lawn);
    }
}

class Zombies extends Actor{
    protected double speed;
    protected backYard lawn;
    protected LawnMower rowLawnMower;
    protected int relocatedY;
    protected int key;
    volatile protected boolean AttackStart = false;
    volatile protected Plants nextPlant = null;
    private boolean Dead = false;

    public Zombies(int posy,int key,backYard l,int ry){
        Category[0] = "Zombies";
        lawn = l;
        relocatedY = ry;
        this.pos = new double[]{1366,posy-relocatedY};
        if(l.getLevel() <= 2)
            key = key - 2;
        else if(l.getLevel() == 3)
            key = key - 1;
        rowLawnMower = lawn.getLawnMowers().get(key-1);
        this.key = (l.getMatrix().getKey()[key-1]);
    }

    public void loadGame(backYard l) throws IOException{
        lawn = l;
        if(Category[1].equals("NormalZombie"))
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/normalZombieWalking.gif"),65,155,false,false));
        else if(Category[1].equals("FlagZombie"))
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/normalZombieWalking.gif"),65,155,false,false));
        else if(Category[1].equals("ConeHeadZombie"))
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/coneheadZombieWalking.gif"),119,170,false,false));
        else
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/footballZombieWalking.gif"),78,160,false,false));
        setLayout();
        lawn.getBackYard().getChildren().add(actorImage);
        setupTimeLine(speed);
    }

    public void findPlants(){
        ArrayList<Plants> rowPlants = lawn.mapOfPlants.get(key);
        if(rowPlants != null)
            for(Plants x: rowPlants)
                if(x.pos[0]+70>this.getPosX() && x.pos[0]<this.getPosX()){
                    nextPlant = x;
                    nextPlant.setZombieAttack(this);
                    AttackStart = true;
                    break;
                }
    }

    @Override
    public void attack(){
        nextPlant.setHealth(Attackpow);
    }

    public void setLayout(){
        this.actorImage.setLayoutY(pos[1]);
        this.actorImage.setLayoutX(pos[0]);
    }

    public double getPosX(){
        return pos[0];
    }

    @Override
    public void act() {
        if(actorImage.getLayoutX() <= 150)
            lostGame();
        if(lawn.pausePlay)
            this.setUpTime.pause();
        if(Health <= 0)
            Dead();
        if(this.pos[0] < 325 && !rowLawnMower.isActive()){
            rowLawnMower.setActive(true);
        }
        if(!AttackStart) {
            findPlants();
            actorImage.setLayoutX(actorImage.getLayoutX()-1);
            this.pos[0] = actorImage.getLayoutX();
        }
        else if(AttackStart)
            attack();
    }

    public void Dead(){
        lawn.getBackYard().getChildren().remove(actorImage);
        Dead = true;
        lawn.mapOfZombies.get(key).remove(this);
        if(nextPlant != null)
            nextPlant.zombieAttack.remove(this);
        setUpTime.stop();
    }

    protected void setupTimeLine(double speed){
        KeyFrame newKey = new KeyFrame(Duration.millis(1000*speed), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();

    }

    public void lostGame(){
        LostGame l = new LostGame(lawn);
        Dead = true;
        System.out.println(123);
        lawn.ps.setScene(new Scene(l.getLostScreen(),1366,768));
        lawn.pausePlay = true;
        this.setUpTime.stop();
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            if(!lawn.pausePlay && !Dead)
                act();
        }
    }
}

class LostGame{
    private Pane lostScreen = new Pane();
    private Button Retry = new Button();
    private Button Level = new Button();
    private backYard yard;

    public LostGame(backYard x){
        try{
            yard = x;
            lostScreen.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/YouLost.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1366,768,false,false,false,false))));
            Retry.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Retry.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(190,68,false,false,false,false))));
            Level.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/exitToLevel.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(220,65,false,false,false,false))));
            Retry.setMinSize(190,68);
            Level.setMinSize(220,65);
            setLayout(Retry,760,660);
            setLayout(Level,360,660);
            lostScreen.getChildren().addAll(Retry,Level);

            Level.setOnMouseEntered(e->{
                Glow g = new Glow();
                g.setLevel(0.4);
                Level.setEffect(g);
            });
            Level.setOnMouseExited(e->{
                Glow g = new Glow();
                g.setLevel(0);
                Level.setEffect(g);
            });
            Level.setOnMousePressed(e->{
               Level newLev = new Level(yard.ps,yard.hp,yard.currentUser);
               yard.ps.setScene(new Scene(newLev.getLevel(),1366,768));
               yard = null;
            });
            Retry.setOnMouseEntered(e->{
                Glow g = new Glow();
                g.setLevel(0.4);
                Retry.setEffect(g);
            });
            Retry.setOnMouseExited(e->{
                Glow g = new Glow();
                g.setLevel(0);
                Retry.setEffect(g);
            });
            Retry.setOnMousePressed(e->{
                yard.setUpTime.stop();
                backYard newLawn = new backYard(yard.getLevel(),yard.hp,yard.ps,yard.currentUser);
                Scene newLawnScene = new Scene(newLawn.getBackYard(),1366,768);
                yard.ps.setScene(newLawnScene);
                newLawn.play();
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public Pane getLostScreen(){return lostScreen;}
    public void setLayout(Node e,int x,int y){
        e.setLayoutX(x);
        e.setLayoutY(y);
    }
}

class NormalZombie extends Zombies{

    public NormalZombie(int posy, int key, backYard x){
        super(posy,key,x,15);
        try{
            Category[1] = "NormalZombie";
            Health = 200;
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/normalZombieWalking.gif"),65,155,false,false));
            speed = 0.05;
            Attackpow = 2;
            this.setLayout();
            setupTimeLine(speed);
        } catch (FileNotFoundException f) {
            System.out.println(f.getMessage());
        }
    }
}

class FlagZombie extends Zombies{
    public FlagZombie(int posy,int key,backYard x) {
        super(posy,key,x,7);
        try {
            Health = 2000;
            Category[1] = "FlagZombie";
            Attackpow = 2;
            speed = 0.0375;
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/boss.gif"),61,145,false,false));
            this.setLayout();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class ConeHeadZombie extends Zombies{
    public ConeHeadZombie(int posy,int key,backYard x){
        super(posy,key,x,30);
        try {
            Category[1] = "ConeHeadZombie";
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/coneheadZombieWalking.gif"),119,170,false,false));
            speed = 0.05;
            Attackpow = 2;
            Health = 360;
            this.setLayout();
            setupTimeLine(speed);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class FootballZombie extends Zombies{
    public FootballZombie(int posy,int key, backYard x){
        super(posy,key,x,20);
        try {
            Category[1] = "FootballZombie";
            actorImage = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/footballZombieWalking.gif"),78,160,false,false));
            setLayout();
            Health = 400;
            speed = 0.025;
            Attackpow = 3;
            setupTimeLine(speed);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class generateMap implements Serializable{
    public static final long serialVersionUID = 44L;
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

class backYard implements Serializable{
    public static final long serialVersionUID = 42L;
    private generateMap matrix;
    private long lastZombieAdded;
    volatile private ArrayList<LawnMower> lawnMowers = new ArrayList<LawnMower>();
    transient private Background backYardImg;
    transient private ArrayList<Icons> availableIcon = new ArrayList<Icons>();
    private int level;
    transient private Label sunCountLabel;
    private long lastAdded = 0;
    transient private Pane backYard;
    private int sunCount = 150;
    transient protected Timeline setUpTime;
    protected boolean pausePlay = false;
    volatile HashMap<Integer,ArrayList<Plants>> mapOfPlants = new HashMap<Integer, ArrayList<Plants>>();
    volatile HashMap<Integer,ArrayList<Zombies>> mapOfZombies = new HashMap<>();
    volatile ArrayList<Sun> suns = new ArrayList<Sun>();
    private int nosOfWavesGenerated = 0;
    private int RechargeTime[] = {10,10,35,20,20};
    volatile private int LastBought[] = new int[5];
    transient Stage ps;
    transient Scene hp;
    private int nosOfZombies[] = new int[3];
    private String[] address;
    private int totalZombies;
    private int zombiesOver = 0;
    transient private Shovel shove = new Shovel(this);
    transient ProgressBar progressBar;
    User currentUser;

    public backYard(int level, Scene x, Stage y, User c){
        try{
            ps = y;
            currentUser = c;
            hp = x;
            backYard = new Pane();
            this.level = level;
            sunCountLabel = new Label();
            progressBar = new ProgressBar();
            if (level<=2){
                backYardImg = new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Level1lawn.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1366,768,false,false,false,false)));
                lawnMowers.add(new LawnMower(new double[]{240,375},3,this));
                matrix = new generateMap(1);
                if (level == 1){
                    address = new String[]{"PeashooterSeed.png"};
                    nosOfZombies = new int[]{2,2,2};
                    totalZombies = 6;
                } else{
                    address = new String[]{"PeashooterSeed.png","SunflowerSeed.png"};
                    nosOfZombies = new int[]{3,3,4};
                    totalZombies = 10;
                }
            }else if (level == 3) {
                backYardImg = new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Level3lawn.png")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1366, 768, false, false, false, false)));
                for(int i = 0;i<3;i++)
                    lawnMowers.add(new LawnMower(new double[]{240,260+i*130},i+2,this));
                matrix = new generateMap(3);
                address = new String[]{"PeashooterSeed.png","SunflowerSeed.png","CherryBombSeed.png"};
                nosOfZombies = new int[]{4,5,7};
                totalZombies = 16;
            }else{
                backYardImg = new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Lawn.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1366,768,false,false,false,false)));
                for(int i = 1;i<6;i++)
                    lawnMowers.add(new LawnMower(new double[]{240,i*130},i,this));
                matrix = new generateMap(5);
                if (level == 4){
                    address = new String[]{"PeashooterSeed.png","SunflowerSeed.png","CherryBombSeed.png","WallnutSeed.png"};
                    nosOfZombies = new int[]{6,10,14};
                    totalZombies = 30;
                }
                else{
                    address = new String[]{"PeashooterSeed.png","SunflowerSeed.png","CherryBombSeed.png","WallnutSeed.png","PotatoMineSeed.png"};
                    nosOfZombies = new int[]{11,14,16};
                    totalZombies = 40;
                }
            }
            for(String el:address)
                availableIcon.add(new Icons(el,new int[]{75,77},this));
            initBackyard();
            this.setupTimeLine();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<LawnMower> getLawnMowers() {
        return lawnMowers;
    }

    public Shovel getShoved(){
        return shove;
    }
    public Pane getBackYard(){
        return backYard;
    }

    public void play(){
        setupTimeLine();
        setUpTime.play();
        lastAdded = System.currentTimeMillis();
        lastZombieAdded = lastAdded;
    }

    public Zombies addZombie(){
        Random zombieGenerator = new Random();
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
                newZ = new FootballZombie(matrix.getFirstColY()[key],matrix.getKey()[key],this);
        }
        key = matrix.getKey()[key];
        System.out.println(key);
        ArrayList<Zombies> z;
        if(!mapOfZombies.containsKey(key))
            z = new ArrayList<Zombies>();
        else{
            z = mapOfZombies.get(key);
        }
        z.add(newZ);
        zombiesOver++;
        mapOfZombies.put(key,z);
        return newZ;
    }

    public void WonGame(){
        this.setUpTime.stop();
        pausePlay = true;
        for(saveLevel x : currentUser.getGames()){
            if(x.getLevelNumber() == this.level) {
                currentUser.getGames().remove(x);
                break;
            }
        }
        currentUser.setLevelPlayable(this.level+1);
        System.out.println(this.level+1);
        Level leve = new Level(ps,hp,currentUser);
        ps.setScene(new Scene(leve.getLevel(),1366,768));
        for(int i = currentUser.getLevelPlayable();i<5;i++)
            leve.but.get(i).setDisable(true);
    }

    public Pane initBackyard() {
        try {
            backYard.setBackground(backYardImg);
            progressBar.setStyle("-fx-text-box-border: black; -fx-control-inner-background: black; -fx-accent: lawngreen");
            setLayout(progressBar,1000,740);
            progressBar.setMinSize(240,30);
            ImageView timer = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Timer.png"), 240, 30, false, false));
            setLayout(timer,1000,740);
            sunCountLabel.getStyleClass().addAll("textField_color", "textField_color_black");
            setLayout(sunCountLabel, 48, 65);
            ImageView iconBar = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/iconBar.png"), 600, 100, false, false));
            setLayout(sunCountLabel, 48, 65);
            setLayout(iconBar, 10, 0);
            setLayout(shove,620,4);
            InGameMenu menuButton = new InGameMenu(this);
            backYard.getChildren().addAll(iconBar, sunCountLabel,shove,progressBar,timer);
            for (LawnMower elements : lawnMowers)
                backYard.getChildren().add(elements.getLawnMowerImg());
            int j = 0;
            for (Icons i : availableIcon) {
                backYard.getChildren().add(i.getIconImage(j));
                j++;
                backYard.getStylesheets().add("/sample/CSSButtonShape.CSS");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backYard;
    }

    public Object setLayout(Object o, double x, double y){
        Node nd = (Node)o;
        nd.setLayoutX(x);
        nd.setLayoutY(y);
        return (Object)nd;
    }

    public void sunFallDrop(){
        Random path = new Random();
        double[] pos = {path.nextInt(900) + 350,path.nextInt(550) + 130};
        Sun s = new Sun(this,pos,false,suns);
        backYard.getChildren().add(s.sunImage);
        suns.add(s);
    }

    public int getLevel() {
        return level;
    }

    public generateMap getMatrix() {
        return matrix;
    }

    public void generateWave(int i){
        int zombiAlive = 0;
        for(Integer k:mapOfZombies.keySet())
            if(mapOfZombies.get(k) != null)
                zombiAlive += mapOfZombies.get(k).size();
        if(i != 0 && nosOfZombies[i-1] == 0 && zombiAlive != 0)
            return;
        if(i != 0 && nosOfZombies[i-1] == 0 && zombiAlive == 0)
            nosOfZombies[i-1] = -1;
        nosOfZombies[i]-=1;
        Zombies newz = addZombie();
        backYard.getChildren().add(newz.actorImage);
        if(nosOfZombies[i] == 0)
            nosOfWavesGenerated++;
    }

    public void act() {
        int size = 0;
        for(Integer k:mapOfZombies.keySet())
            size += mapOfZombies.get(k).size();
        if(zombiesOver == totalZombies && size == 0)
            WonGame();
        long curTime = System.currentTimeMillis();
        sunCountLabel.setText(sunCount+"");
        if(curTime>=lastAdded + 20000) {
            lastAdded = curTime;
            sunFallDrop();
        }
        Random time = new Random();
        if(curTime >= lastZombieAdded + (time.nextInt(30)+4)*1000){
            for(int i = 0;i<3;i++)
                if(nosOfZombies[i]>0){
                    generateWave(i);
                    break;
                }
            lastZombieAdded = curTime;
        }
        progressBar.setProgress(((double)zombiesOver)/totalZombies);
        for(Icons e:availableIcon){
            if(LastBought[e.getInd()] == RechargeTime[e.getInd()] && sunCount>=e.getPrice())
                e.setDisable(false);
            else
                e.setDisable(true);
        }
        for(int i = 0;i<5;i++)
            if(LastBought[i] < RechargeTime[i])
                LastBought[i]++;

    }

    public void setSunCount(int sunCount) {
        this.sunCount += sunCount;
    }

    public void setBg() throws IOException{
        if(level < 3)
            backYardImg = new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Level1lawn.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1366,768,false,false,false,false)));
        else if(level == 3)
            backYardImg = new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Level3lawn.png")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1366, 768, false, false, false, false)));
        else
            backYardImg = new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Lawn.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1366,768,false,false,false,false)));
    }

    public void loadGame(Stage s, Scene x){
        try{
            pausePlay = false;
            backYard = new Pane();
            sunCountLabel = new Label();
            availableIcon = new ArrayList<Icons>();
            shove = new Shovel(this);
            progressBar = new ProgressBar();
            setBg();
            this.ps = s;
            this.hp = x;
            for(String el:address)
                availableIcon.add(new Icons(el,new int[]{75,77},this));
            for(LawnMower l:lawnMowers){
                l.loadGame(this);
            }
            initBackyard();
            for(Sun sun:suns)
                sun.LoadGame();
            for(Integer e: mapOfPlants.keySet())
                for(Plants plant:mapOfPlants.get(e))
                    plant.loadGame(this);
            for(Integer e: mapOfZombies.keySet())
                for(Zombies zombie:mapOfZombies.get(e))
                    zombie.loadGame(this);
            setupTimeLine();
            play();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setupTimeLine(){
        KeyFrame newKey = new KeyFrame(Duration.seconds(1), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }

    public void setLastBought(int i) {
        LastBought[i] = 0;
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            if(!pausePlay)
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
    private int indOfBuy;

    public Icons(String address,int size[], backYard x) throws FileNotFoundException{
        lawn = x;
        map = lawn.getMatrix();
        if (address.equals("PeashooterSeed.png")){
            IconType = "peaShooter.gif";
            type = new PeaShooter(x);
            translatePos = type.translatePos;
            indOfBuy = 0;
        }
        else if(address.equals("WallnutSeed.png")){
            IconType = "Wallnut.png";
            type = new WallNut(x);
            translatePos = type.translatePos;
            indOfBuy = 3;
        }
        else if(address.equals("SunflowerSeed.png")){
            IconType = "SunFlower.gif";
            type = new SunFlower(x);
            translatePos = type.translatePos;
            indOfBuy = 1;
        }
        else if(address.equals("PotatoMineSeed.png")) {
            IconType = "PotatoMine.gif";
            type = new PotatoMine(x);
            translatePos = type.translatePos;
            indOfBuy = 4;
        }else{
            IconType = "CherryBomb.gif";
            type = new CherryBomb(x);
            translatePos = type.translatePos;
            indOfBuy = 2;
        }
        iconImage = new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/"+address));
        this.setMinSize(size[0],size[1]);
        this.setBackground(new Background(new BackgroundImage(iconImage,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(size[0],size[1],false,false,false,false))));
        this.setOnMouseEntered(e->{
            Glow g = new Glow();
            g.setLevel(0.2);
            this.setEffect(g);
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
                copy.addToLawn(new double[]{e.getSceneX()-30,e.getSceneY()-20});
                this.copy = copy;
            }catch(Exception ex){
                ex.printStackTrace();
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
            if(key == 6)
                key = 5;
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
                System.out.println(key+" plants ");
                copy.start();
                lawn.setSunCount(-getPrice());
                lawn.setLastBought(indOfBuy);
            }else{
                lawn.getBackYard().getChildren().remove(copy.actorImage);
            }
        });
    }

    public int getInd(){
        return indOfBuy;
    }
    public Icons getIconImage(int x){
        this.setLayoutX(120+80*x);
        this.setLayoutY(10);
        return this;
    }

    public int getPrice(){
        return type.price;
    }
}

class InGameMenu{
    private static backYard lawn2;
    private backYard lawn;
    private Button menuButton = new Button();
    protected Timeline setUpTime;
    private Button backToMenu = new Button();
    private ImageView pauseMenu;
    private ImageView BackToGame;
    private ImageView SaveGame;
    private ImageView RestartLevel;

    public InGameMenu(backYard lawn){
        this.lawn = lawn;
        lawn2 = lawn;
        try{
            menuButton.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/InGameMenuButton.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT,new BackgroundSize(131,41,false,false,false,false))));
            backToMenu.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/mainMenuButton.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT,new BackgroundSize(219,49,false,false,false,false))));
            BackToGame = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/BackToGame.png"),357,84,false,false));
            SaveGame = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/SaveGame.png"),219,49,false,false));
            RestartLevel = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/restartLevel.png"),219,49,false,false));
            setLayout(RestartLevel,610,299);
            setLayout(SaveGame,610,239);
            setLayout(backToMenu,610,362);
            setLayout(BackToGame,540,509);
            backToMenu.setMinSize(219,49);
            setLayout(menuButton,1200,0);
            menuButton.setMinSize(131,41);
            this.lawn.getBackYard().getChildren().add(menuButton);
            setupTimeLine();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setLayout(Node e,int x,int y){
        e.setLayoutX(x);
        e.setLayoutY(y);
    }

    public void serialize(String name) throws IOException {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream("src/sample/out.txt"));
            ArrayList<User> x = Main.UserArrayList;
            for(User b: x)
                if(b.getName().equalsIgnoreCase(name)){
                    boolean flag = true;
                    for(saveLevel s: b.getGames())
                        if(s.getLevelNumber() == lawn.getLevel()){
                            s.setLawn(lawn);
                            flag = false;
                        }
                    if(flag){
                        saveLevel s = new saveLevel(lawn);
                        b.add(s);
                    }
                    System.out.println("Saving at User" + b.getName());
                }
            out.writeObject(x);
        }finally {
            out.close();
        }
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
                    lawn.getShoved().setDisable(true);
                    lawn.setUpTime.pause();
                    lawn.pausePlay = true;
                    pauseMenu = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/InGameMenu.png"),435,506,false,false));
                    setLayout(pauseMenu, 500,100);
                    lawn.getBackYard().getChildren().addAll(pauseMenu,backToMenu,BackToGame,RestartLevel,SaveGame);
                }catch (FileNotFoundException f){
                    f.printStackTrace();
                }
            });
            BackToGame.setOnMousePressed(e->{
                lawn.getBackYard().getChildren().removeAll(BackToGame,backToMenu,pauseMenu,RestartLevel,SaveGame);
                lawn.setUpTime.play();
                lawn.pausePlay = false;
                menuButton.setDisable(false);
                lawn.getShoved().setDisable(false);
            });
            RestartLevel.setOnMousePressed(e->{
                lawn.getBackYard().getChildren().removeAll(BackToGame,backToMenu,pauseMenu,RestartLevel,SaveGame);
                lawn.setUpTime.stop();
                backYard newLawn = new backYard(lawn.getLevel(),lawn.hp,lawn.ps,lawn.currentUser);
                Scene newLawnScene = new Scene(newLawn.getBackYard(),1366,768);
                lawn.ps.setScene(newLawnScene);
                setUpTime.stop();
            });
            SaveGame.setOnMousePressed(e->{
                try{
                    serialize(lawn.currentUser.getName());
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            });
            backToMenu.setOnMousePressed(e->{
                System.out.println(lawn.hp);
                lawn.ps.setScene(lawn.hp);
                lawn.getBackYard().getChildren().removeAll(BackToGame,backToMenu,pauseMenu,RestartLevel,SaveGame);
                lawn.setUpTime.stop();
                System.out.println(123);
            });
        }
    }
}

class LawnMower implements Serializable{
    public static final long serialVersionUID = 45L;
    private boolean active = false;
    transient private ImageView lawnMowerImg;
    private int sunCount = 0;
    private double pos[] = new double[2];
    private backYard lawn;
    private int Row;
    transient Timeline setUpTime;

    public LawnMower(double[] setPos, int Row, backYard lawn) throws FileNotFoundException {
        lawnMowerImg = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Lawnmower.png"),125,90,false,false));
        lawnMowerImg.setLayoutX(setPos[0]);
        lawnMowerImg.setLayoutY(setPos[1]);
        this.pos = setPos;
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

    public void loadGame(backYard lawn) throws IOException{
        this.lawn = lawn;
        lawnMowerImg = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Lawnmower.png"),125,90,false,false));
        lawnMowerImg.setLayoutX(pos[0]);
        lawnMowerImg.setLayoutY(pos[1]);
        setupTimeLine();
    }

    public void setupTimeLine(){
        KeyFrame newKey = new KeyFrame(Duration.millis(1000*0.004), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }

    private class TimeHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            if(active && !lawn.pausePlay){
                lawnMowerImg.setLayoutX(lawnMowerImg.getLayoutX()+1);
                pos[0] = lawnMowerImg.getLayoutX();
                ArrayList<Zombies> z = lawn.mapOfZombies.containsKey(Row)?lawn.mapOfZombies.get(Row):null;
                if(z != null && z.size() != 0){
                    for(Zombies x:z)
                        if(Math.abs(x.getPosX()-lawnMowerImg.getLayoutX()) == 4)
                            x.setHealth(x.getHealth());
                }

            }
            if(lawnMowerImg.getX()>1366)
                setUpTime.stop();
        }
    }
}

class Sun implements Serializable{
    public static final long serialVersionUID = 46L;
    transient protected ImageView sunImage;
    private double speed = 0.01;
    private double pos[] = {0,0};
    transient Timeline setUpTime;
    private backYard lawn;
    private boolean sunFlowerProduce = false;
    private ArrayList<Sun> arraySun;

    public Sun(backYard lawn, double[] pos,boolean value,ArrayList<Sun> t) {
        try{
            arraySun = t;
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
            e.printStackTrace();
        }
    }

    public void setupTimeLine() {
        KeyFrame newKey = new KeyFrame(Duration.millis(speed*1000), new TimeHandler());
        setUpTime = new Timeline(newKey);
        setUpTime.setCycleCount(Timeline.INDEFINITE);
        setUpTime.play();
    }

    public void LoadGame() throws IOException{
        sunImage = new ImageView(new Image(new FileInputStream( "src/sample/PlantsVsZombies_Images/sun.gif"),45,45,false,false));
        sunImage = (ImageView) lawn.setLayout(sunImage,pos[0],pos[1]);
        lawn.getBackYard().getChildren().add(this.sunImage);
        setupTimeLine();
    }
    private class TimeHandler implements EventHandler<ActionEvent>{
        private boolean mousePressed = false;
        public void handle(ActionEvent event){
            if(lawn.pausePlay)
                return;
            if (sunImage.getLayoutY() < pos[1]+10 && !mousePressed) {
                sunImage.setLayoutY(sunImage.getLayoutY()+1);
                pos[0] = sunImage.getLayoutX();
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
                    lawn.suns.remove(this);
                    setUpTime.stop();
                });
            });
        }
    }
}

class Shovel extends Button{
    transient private ImageView shovelImage;
    private backYard lawn;
    transient private Glow glowOnB = new Glow();

    public Shovel(backYard x){
        try{
            lawn = x;
            shovelImage = new ImageView(new Image(new FileInputStream( "src/sample/PlantsVsZombies_Images/ShovelRemove.png"),100,100,false,false));
            this.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Shovel.jpg")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(85,90,false,false,false,false))));
            this.setMinSize(85,90);
            this.setOnMouseEntered(e->{
                glowOnB.setLevel(0.4);
                this.setEffect(glowOnB);
            });
            this.setOnMouseExited(e->{
                glowOnB.setLevel(0);
                this.setEffect(glowOnB);
            });
            this.setOnMousePressed(e->{
                e.setDragDetect(true);
                lawn.setLayout(shovelImage,e.getSceneX(),e.getSceneY());
                lawn.getBackYard().getChildren().add(shovelImage);
            });
            this.setOnMouseDragged(e->{
                e.setDragDetect(false);
                lawn.setLayout(shovelImage,e.getSceneX(),e.getSceneY());
            });
            this.setOnMouseReleased(e->{
                int pos[] = lawn.getMatrix().findPos(shovelImage.getLayoutX(),shovelImage.getLayoutY(),new double[]{0,0});
                int key = (pos[1]-15)/100;
                if (key == 0) {
                    key = 1;
                }
                ArrayList<Plants> p = lawn.mapOfPlants.containsKey(key)?lawn.mapOfPlants.get(key):null;
                if(pos[0] == 0 || p == null){
                    lawn.getBackYard().getChildren().remove(shovelImage);
                    return;
                }
                for(Plants z:p){
                    if(z.pos[0] - z.translatePos[0] == pos[0]){
                        z.setHealth(z.getHealth());
                    }
                }
                lawn.getBackYard().getChildren().remove(shovelImage);
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
class User implements Serializable{
    public static final long serialVersionUID = 47L;
    private String name;
    private int levelPlayable = 1;
    private ArrayList<saveLevel> games = new ArrayList<saveLevel>(0);

    public User(String s){
        name = s;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevelPlayable() {
        return levelPlayable;
    }
    public ArrayList<saveLevel> getGames(){
        return games;
    }

    public void add(saveLevel lvl){
        boolean flag = true;
        for(saveLevel e:games)
            if(e.getLevelNumber() == lvl.getLevelNumber()){
                e = lvl;
                flag = false;
                break;
            }
        if(flag)
            games.add(lvl);
    }

    public void setLevelPlayable(int levelPlayable) {
        if(this.levelPlayable<levelPlayable)
            this.levelPlayable = levelPlayable;
    }

}

class saveLevel implements Serializable{
    public static final long serialVersionUID = 48L;
    private backYard Lawn;
    private int LevelNumber;

    public saveLevel(backYard element){
        Lawn = element;
        LevelNumber = Lawn.getLevel();
    }

    public int getLevelNumber(){
        return LevelNumber;
    }

    public backYard getLawn(){
        return Lawn;
    }

    public void setLawn(backYard x){
        Lawn = x;
    }
}

class Level {
    private Pane level;
    private Background levelImg;
    private Stage st;
    private Scene mainLawnScene;
    private backYard mainLawn;
    private Scene HomepageScene;
    private String name;
    private boolean LevelThere = false;
    private User currentUser;
    Button level1Btn = new Button();
    Button level2Btn = new Button();
    Button level3Btn = new Button();
    Button level4Btn = new Button();
    Button level5Btn = new Button();
    ArrayList<Button> but =  new ArrayList<Button>();

    public void activateLevel(int lvl){
        mainLawn = LoadGame(lvl);
        if(LevelThere)
            mainLawn.loadGame(st,HomepageScene);
        else
            mainLawn.play();
        mainLawnScene = new Scene(mainLawn.getBackYard(),1366,768);
        st.setScene(mainLawnScene);
        LevelThere = false;
    }

    public Level(Stage primaryStage, Scene hp, User x){
        try{
        level = new Pane();
        currentUser = x;
        this.name = name;
        levelImg = new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/levelBackground.png")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1366, 768, false, false, false, false)));
        level.setBackground(levelImg);
        this.st = primaryStage;
        this.HomepageScene = hp;
        but.add(level1Btn);
        but.add(level2Btn);
        but.add(level3Btn);
        but.add(level4Btn);
        but.add(level5Btn);

        for(int i = currentUser.getLevelPlayable();i<5;i++)
            but.get(i).setDisable(true);
        BackgroundImage level1img = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/lvl1.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(150,220,false,false,false,false));
        BackgroundImage level2img = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/lvl2.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(150,220,false,false,false,false));
        BackgroundImage level3img = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/lvl3.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(150,220,false,false,false,false));
        BackgroundImage level4img = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/lvl4.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(150,220,false,false,false,false));
        BackgroundImage level5img = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/lvl5.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(150,220,false,false,false,false));

        level1Btn.setBackground(new Background(level1img));
        level1Btn.setLayoutY(250);
        level1Btn.setLayoutX(160);
        level1Btn.setMinSize(150,220);
        this.getLevel().getChildren().add(level1Btn);

        level1Btn.setOnMouseClicked(e->{
            this.activateLevel(1);
        });

        level2Btn.setBackground(new Background(level2img));
        level2Btn.setLayoutY(250);
        level2Btn.setLayoutX(380);
        level2Btn.setMinSize(150,220);
        this.getLevel().getChildren().add(level2Btn);

        level2Btn.setOnMouseClicked(e->{
            this.activateLevel(2);
        });

        level3Btn.setBackground(new Background(level3img));
        level3Btn.setLayoutY(250);
        level3Btn.setLayoutX(600);
        level3Btn.setMinSize(150,220);
        this.getLevel().getChildren().add(level3Btn);

        level3Btn.setOnMouseClicked(e->{
            this.activateLevel(3);
        });

        level4Btn.setBackground(new Background(level4img));
        level4Btn.setLayoutY(250);
        level4Btn.setLayoutX(820);
        level4Btn.setMinSize(150,220);
        this.getLevel().getChildren().add(level4Btn);

        level4Btn.setOnMouseClicked(e->{
            this.activateLevel(4);
        });

        level5Btn.setBackground(new Background(level5img));
        level5Btn.setLayoutY(250);
        level5Btn.setLayoutX(1040);
        level5Btn.setMinSize(150,220);
        this.getLevel().getChildren().add(level5Btn);

        level5Btn.setOnMouseClicked(e->{
            this.activateLevel(5);
        });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public backYard LoadGame(int level){
        for (saveLevel y : currentUser.getGames()){
            if (y.getLevelNumber() == level) {
                System.out.println(LevelThere);
                LevelThere = true;
                return y.getLawn();
            }
        }
        return new backYard(level,HomepageScene,st,currentUser);
    }

    public Pane getLevel() {
        return level;
    }

    public void setCurrentUser(User x){
        currentUser = x;
    }
//    public void setUpTimeLine(){
//            KeyFrame newKey = new KeyFrame(Duration.millis(1000), new TimeHandler());
//            Timeline setUpTime = new Timeline(newKey);
//            setUpTime.setCycleCount(Timeline.INDEFINITE);
//            setUpTime.play();
//    }

    public void setScene(Scene hps){
        HomepageScene = hps;
    }
//
//    private class TimeHandler implements EventHandler<ActionEvent>{
//        public void handle(ActionEvent event){
//        }
//    }

}

class ChooseUser{
    private Pane UserChooser = new Pane();
    private Button UserOk = new Button();
    private TextField UserNameTf = new TextField();
    private ImageView Userbg;
    private Stage st;
    private Glow g = new Glow();
    private Light.Spot light = new Light.Spot();

    public ChooseUser(Stage s){
        try {
            st = s;
            UserChooser.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/NewUserbg.jpg")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1366, 768, false, false, false, false))));
            UserOk.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/NewUserok.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,BackgroundSize.DEFAULT)));
            Userbg = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/New_User.png")));
            UserNameTf.setMinSize(438,60);
            UserNameTf.setAlignment(Pos.CENTER);
            UserOk.setMinSize(216,47);
            setLayout(UserOk,605,390);
            setLayout(Userbg,400,100);
            setLayout(UserNameTf,492,288);
            UserNameTf.lengthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if (newValue.intValue() > oldValue.intValue())
                        if (UserNameTf.getText().length() >= 26)
                            UserNameTf.setText(UserNameTf.getText().substring(0, 26));
                }
            });
            UserNameTf.getStyleClass().add("textField_color");
            UserChooser.getStylesheets().add("/sample/CSSButtonShape.CSS");
            UserChooser.getChildren().addAll(Userbg,UserOk,UserNameTf);

            UserOk.setOnMouseEntered(e->{
                g.setLevel(0.3);
                UserOk.setEffect(g);
            });
            UserOk.setOnMouseExited(e->{
                g.setLevel(0);
                UserOk.setEffect(g);
            });
            UserOk.setOnAction(e->{
                Main.UserName = UserNameTf.getText();
                if(!Main.UserName.equals("")) {
                    User us = getUser(Main.UserName);
                    Hompage Hp = new Hompage(st,us);
                    Scene hps = new Scene(Hp.getHomePage(),1366,768);
                    Hp.setScene(hps);
                    st.setScene(hps);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public User getUser(String name){
        for(User x : Main.UserArrayList)
            if(name.equals(x.getName()))
                return x;
        User newU = new User(name);
        Main.UserArrayList.add(newU);
        return newU;
    }
    public Pane getUserChooser(){
        return UserChooser;
    }

    public void setLayout(Node e,int x,int y){
        e.setLayoutY(y);
        e.setLayoutX(x);
    }
}

class Hompage{
    private Pane HomePage = new Pane();
    private Stage st;
    private Label UserLabel = new Label();
    private Button adventureButton = new Button();
    private Label LevelTf = new Label();
    private Scene HomepageScene;
    private Level LevelChooser;
    private boolean LevelThere = false;
    private Button ChooseDifferentUser = new Button();
    private Glow g = new Glow();
    private User currentUser;
    private Scene levelchooser;
    private Button exitButton = new Button();
    private Button Almanac = new Button();
    public Hompage(Stage s, User cu){
        try{
            st = s;
            currentUser = cu;
            LevelChooser = new Level(st,HomepageScene,currentUser);
            HomePage.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/HomePage.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1376,768,false,false,false,false))));
            UserLabel.setText(currentUser.getName());
            adventureButton.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/AdventureMode.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(555,155,false,false,false,false))));
            ChooseDifferentUser.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/loadUser.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(500,70,false,false,false,false))));
            ChooseDifferentUser.setMinSize(500,70);
            exitButton.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/quit.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(118,84,false,false,false,false))));
            exitButton.setMinSize(118,84);
            Almanac.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/help.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(98,60,false,false,false,false))));
            Almanac.setMinSize(98,60);
            setLayout(exitButton,1215,630);
            setLayout(Almanac,1105,653);
            LevelTf.setText("New Game");
            LevelTf.getStyleClass().add("textField_colorLevel");
            LevelTf.setAlignment(Pos.CENTER);
            LevelTf.setMinSize(140,40);
            LevelTf.setMaxSize(140,40);
            LevelTf.setRotate(5);
            adventureButton.setMinSize(555,155);
            adventureButton.setRotate(5);
            UserLabel.setMinSize(400,60);
            UserLabel.setMaxSize(600,60);
            UserLabel.setAlignment(Pos.CENTER);
            UserLabel.getStyleClass().add("text_UserName");
            setLayout(adventureButton,700,100);
            setLayout(LevelTf,871,205);
            setLayout(UserLabel,94,90);
            setLayout(ChooseDifferentUser,40,160);
            HomePage.getStylesheets().add("/sample/CSSButtonShape.CSS");
            HomePage.getChildren().addAll(adventureButton,LevelTf,UserLabel,ChooseDifferentUser,exitButton, Almanac);

            Almanac.setOnMouseEntered(e->{
                Glow g1 = new Glow();
                g1.setLevel(0.4);
                Almanac.setEffect(g1);
            });
            Almanac.setOnMousePressed(e->{
                AlmanacMethod();
            });
            Almanac.setOnMouseExited(e->{
                Glow g1 = new Glow();
                g1.setLevel(0);
                Almanac.setEffect(g1);
            });
            exitButton.setOnMouseEntered(e->{
                Glow g1 = new Glow();
                g1.setLevel(0.4);
                exitButton.setEffect(g1);
            });
            exitButton.setOnMousePressed(e->{
                System.exit(0);
            });
            exitButton.setOnMouseExited(e->{
                Glow g1 = new Glow();
                g1.setLevel(0);
                exitButton.setEffect(g1);
            });
            ChooseDifferentUser.setOnMouseEntered(e->{
                g.setLevel(0.4);
                ChooseDifferentUser.setEffect(g);
            });
            ChooseDifferentUser.setOnMouseExited(e->{
                g.setLevel(0);
                ChooseDifferentUser.setEffect(g);
            });
            ChooseDifferentUser.setOnMousePressed(e->{
                for(int i = 0;i<Main.UserArrayList.size();i++){
                    if(Main.UserArrayList.get(i).getName().equals(currentUser.getName())){
                        if(i == Main.UserArrayList.size()-1)
                            currentUser = Main.UserArrayList.get(0);
                        else
                            currentUser = Main.UserArrayList.get(i+1);
                        break;
                    }
                }
                Main.UserName = currentUser.getName();
                LevelChooser.setCurrentUser(currentUser);
                UserLabel.setText(currentUser.getName());
            });
            LevelTf.setOnMouseClicked(e->{
                if (LevelTf.getText().equals("New Game")){
                    LevelTf.setText("Choose Level");
                }else{
                    LevelTf.setText("New Game");
                }
            });
            adventureButton.setOnMouseEntered(e->{
                try {
                    adventureButton.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/AdventureMode2.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(555,155,false,false,false,false))));
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            });

            adventureButton.setOnMouseExited(e->{
                try {
                    adventureButton.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/AdventureMode.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(555,155,false,false,false,false))));
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            });
            adventureButton.setOnMousePressed(e->{
                if (LevelTf.getText().equals("New Game")){
                    backYard loaded = LoadGame();
                    if(LevelThere)
                        loaded.loadGame(st,HomepageScene);
                    else
                        loaded.play();
                    Scene gamePlay = new Scene(loaded.getBackYard(),1366,768);
                    st.setScene(gamePlay);
                    LevelThere = false;
                }
                else if (LevelTf.getText().equals("Choose Level")){
                    System.out.println(currentUser.getLevelPlayable());
                    for(int i = 0;i<5;i++)
                        LevelChooser.but.get(i).setDisable(i>=currentUser.getLevelPlayable());
                    st.setScene(levelchooser);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Pane getHomePage() {
        return HomePage;
    }
    public void AlmanacMethod(){
        try{
            Pane almanacPage = new Pane();
            Button exitB = new Button();
            almanacPage.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Almanac.jpg")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1366,768,false,false,false,false))));
            ArrayList<ImageView> seedPackets = new ArrayList<ImageView>(5);
            ImageView card = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/Almanac_PlantCard.png"), 400, 600, false, false));
            setLayout(card,900,140);
            TextArea Info = new TextArea();
            ImageView card2 = new ImageView();
            Info.setMaxSize(350,340);
            Info.setDisable(true);
            Info.setWrapText(true);
            Info.setWrapText(true);
            setLayout(Info,920,400);
            exitB.setBackground(new Background((new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/back.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(40,40,false,false,false,false)))));
            exitB.setMinSize(40,40);
            setLayout(exitB,30,20);
            String address[] = new String[]{"PeashooterSeed.png","SunflowerSeed.png","CherryBombSeed.png","WallnutSeed.png","PotatoMineSeed.png"};
            for(int i = 0;i<5;i++) {
                ImageView el = new ImageView(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/" + address[i]), 160, 240, false, false));
                setLayout(el, 50 + i * 200, 140);
                if(i>2)
                    setLayout(el,50+(i-3)*200,440);
                seedPackets.add(el);
                almanacPage.getChildren().add(el);
            }
            seedPackets.get(1).setOnMousePressed(e->{
                try{
                    Info.setText("Sunflower\n\nSunflowers are tactically the most important plant and the only sun produccing plant, they give u an edge to have more sun to buy more plants.\n\nGo kill those zombies...!");
                    card2.setImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/SunFlower.gif"), 120, 165, false, false));
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            });
            seedPackets.get(3).setOnMousePressed(e->{
                Info.setText("Wall-nut\n\nWall-nuts are your first block of defense,they will sacrifice themselves to save your line,salute to them for this\npatriot act, hard shells; hard life.");
                try {
                    card2.setImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/walnutFull.gif"), 120, 165, false, false));
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            });
            seedPackets.get(0).setOnMousePressed(e->{
                Info.setText("Peashooter\n\n" +
                        "Peashooters are the most basic attack type plant, who shoot pea as bullets so you dont need to touch the\n" +
                        "zombie to kill them, kill them from away snipe it ,kill it.");
                try {
                    card2.setImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/peaShooter.gif"), 120, 165, false, false));
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            });
            seedPackets.get(2).setOnMousePressed(e->{
                Info.setText("Cherry Bomb\n\n" +
                        "Cherry Bombs can blow up all zombies in an area. They have a short fuse so plant them near zombies.\n\n" +
                        "As sweet as he look, he explodes quiet atomically, he is desendent of the 1914 little boy, place him up so that\n" +
                        "he can blow. “Zombies are my friends” - he says");
                try {
                    card2.setImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/cherryBomb.png"), 140, 165, false, false));
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            });
            seedPackets.get(4).setOnMousePressed(e->{
                Info.setText("Potato Mine\n\n" +
                        "Landmines are old fashioned. “grandpops were dumb, I am a smart mine I can pull off and zombie’s head.”\n\n" +
                        "Potato mines are sensitive little thing, if you touch them they will explode.But they are lazy things need time to\n" +
                        "buckle up.");
                try {
                    card2.setImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/PotatoMine(2).png"), 120, 165, false, false));
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            });

            exitB.setOnMousePressed(e->{
                st.setScene(HomepageScene);
            });
            setLayout(card2,1020,160);
            almanacPage.getChildren().addAll(card,Info,card2,exitB);
            Info.getStyleClass().add("text_UserName2");
            Scene almanacScene = new Scene(almanacPage,1366,768);
            almanacScene.getStylesheets().add("sample/CSSButtonShape.CSS");
            st.setScene(almanacScene);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public backYard LoadGame(){
        for (saveLevel y : currentUser.getGames()){
            if (y.getLevelNumber() == currentUser.getLevelPlayable()) {
                LevelThere = true;
                return y.getLawn();
            }
        }
        return new backYard(currentUser.getLevelPlayable(),HomepageScene,st,currentUser);
    }

    public void setScene(Scene hps){
        HomepageScene = hps;
        LevelChooser.setScene(hps);
        levelchooser = new Scene(LevelChooser.getLevel(),1366,768);
    }

    public void setLayout(Node e,int x,int y){
        e.setLayoutY(y);
        e.setLayoutX(x);
    }
}

class NoMusicException extends Exception{
    NoMusicException(String message){
        super(message);
    }
}

public class Main extends Application {
    static String UserName;
    static ArrayList<User> UserArrayList;

    public void deserialize() throws IOException,ClassNotFoundException{
        ObjectInputStream input = null;
        try{
            input = new ObjectInputStream(new FileInputStream("src/sample/out.txt"));
            UserArrayList = (ArrayList<User>)input.readObject();
        }catch (Exception e){
            UserArrayList = new ArrayList<User>();
        } finally {
            if(input != null)
                input.close();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("C:\\Users/mohni/IdeaProjects/src/sample/PvZmusic.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        catch (FileNotFoundException e){
            throw new NoMusicException("No Music File Found");
        }



        deserialize();
        primaryStage.setTitle("Plants Vs Zombies");
        Pane LoadingPane = new Pane();
        Image main = new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/MainBack.jpg"));
        BackgroundImage bgMain = new BackgroundImage(main, BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,new BackgroundSize(1376,768.,false,false,false,false));
        LoadingPane.setBackground(new Background(bgMain));

        ChooseUser user = new ChooseUser(primaryStage);
        //Creating Loading Page
        Button b = new Button();
        b.setLayoutX(480);
        b.setLayoutY(678);
        b.setMinSize(410,63);
        b.setOpacity(0);
        BackgroundImage bLoad = new BackgroundImage(new Image(new FileInputStream("src/sample/PlantsVsZombies_Images/LoadGame.png")),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(418,58,false,false,false,false));
        b.setBackground(new Background(bLoad));
        LoadingPane.getChildren().add(b);

        b.setOnMouseEntered(e->{
            b.setOpacity(100);

        });
        b.setOnMouseExited(e->{
            b.setOpacity(0);
        });
        b.setOnMouseClicked(e->{
            primaryStage.setScene(new Scene(user.getUserChooser(),1366,768));
        });

        primaryStage.setScene(new Scene(LoadingPane,1366,768));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
