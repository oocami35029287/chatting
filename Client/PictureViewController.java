import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.event.*;
import java.io.File;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.*;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Optional;
public class PictureViewController{
    @FXML private Button left;
    @FXML private Button right;
    @FXML private Button edit;
    @FXML private Button copy;
    @FXML public  Canvas canvas;
    @FXML private VBox editPane;
    private String pic;
    private Stage stage;
    private GraphicsContext gc;
    private Image img;
    private Image originalImg;
    private double paraOfH;
    private double paraOfW;
    private double para;
    private double index=1.0;
    private double prex;
    private double prey;
    private int i=0;
    private int j=0; 
    private int numcount[]=new int[1000000];
    private double mx=0;
    private double my=0;
    private AccessVBox vbox=new AccessVBox();
    private boolean editting=false; 
    
    public void setPicture(AccessVBox vbox,AccessBox box,Image img,Stage stage){
        
        
        this.vbox=vbox;
        vbox.setCurrentIndex(box);
        vbox.setButton(left,right);
        this.img=img;
        this.stage=stage;
        gc = canvas.getGraphicsContext2D();
        paraOfH=(double)400/(double)img.getHeight();
        paraOfW=(double)600/(double)img.getWidth();
        if(paraOfH>paraOfW)para=paraOfW;else para=paraOfH;
        gc.drawImage(img,300-(int)(img.getWidth()*para)/2,200-(int)(img.getHeight()*para)/2,(int)(img.getWidth()*para),(int)(img.getHeight()*para));
        //***************************
        
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        //********************************
        }    
    
    @FXML
    private void editClicked(ActionEvent e){
        if(editting==false){
            originalImg=img;
            gc.clearRect(0,0,600,400);
            mx=0;my=0;index=1.0;
            paraOfH=(double)400/(double)img.getHeight();
            paraOfW=(double)600/(double)img.getWidth();
            if(paraOfH>paraOfW)para=paraOfW;else para=paraOfH;
            gc.drawImage(img,(300-(int)(img.getWidth()*para*index)/2)+mx,(200-(int)(img.getHeight()*para*index)/2)+my,
                        ((int)(img.getWidth()*para*index))+mx/2,((int)(img.getHeight()*para*index))+my/2); 
            editPane.setVisible(true);
            edit.setText("save");
            editting=true;
        }
        else if(editting==true){
            
            final Alert alert = new Alert(AlertType.CONFIRMATION); 
            alert.setHeaderText(""); //設定對話框視窗裡的標頭文字。若設為空字串，則表示無標頭
            alert.setContentText("要儲存嗎?"); //設定對話框的訊息文字
            final Optional<ButtonType> opt = alert.showAndWait();
            final ButtonType rtn = opt.get(); //可以直接用「alert.getResult()」來取代
            System.out.println(rtn);
            if (rtn == ButtonType.OK) {
                
                //***************************save picture
                File outputFile = new File("./storage/test.png");
                BufferedImage bImage = SwingFXUtils.fromFXImage(img, null);
                try {
                  ImageIO.write(bImage, "png", outputFile);
                } catch (IOException ee) {
                  throw new RuntimeException(ee);
                }    
            } 
            else if(rtn == ButtonType.CANCEL){
                img=originalImg;
                gc.clearRect(0,0,600,400);
                gc.drawImage(img,(300-(int)(img.getWidth()*para*index)/2)+mx,(200-(int)(img.getHeight()*para*index)/2)+my,
                        ((int)(img.getWidth()*para*index))+mx/2,((int)(img.getHeight()*para*index))+my/2); 
            }
            editPane.setVisible(false);
            edit.setText("edit");
            editting=false;
            
            
            
        }
        
        
    }

    @FXML
    private void zoomInOut(ScrollEvent e){
        if(editting==false){
            if(e.getDeltaY()>0){
                if(index<=2)
                    index+=0.1;
            }
            else{
                if(index>=0.5)
                    index-=0.1;
            }
            gc.clearRect(0,0,600,400);
                gc.drawImage(img,(300-(int)(img.getWidth()*para*index)/2)+mx,(200-(int)(img.getHeight()*para*index)/2)+my,
                        ((int)(img.getWidth()*para*index))+mx/2,((int)(img.getHeight()*para*index))+my/2);     
        }
           
    }
    @FXML
    private void movepicture(MouseEvent e){
        
        if(prex>0 && prey>0){
            if(editting==false){    
                mx=mx+e.getX()-prex;
                my=my+e.getY()-prey;
                gc.clearRect(0,0,600,400);
                gc.drawImage(img,(300-(int)(img.getWidth()*para*index)/2)+mx,(200-(int)(img.getHeight()*para*index)/2)+my,
                        ((int)(img.getWidth()*para*index)),((int)(img.getHeight()*para*index)));  
                i++;
            }
            else{
                gc.beginPath();
                gc.moveTo(prex,prey);
                gc.stroke();
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            }
        }
        prex=e.getX();prey=e.getY(); 
        
        
    }
    @FXML
    private void releasingAreaMouse(MouseEvent e){
        if(editting==true){
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            Image snapshot = canvas.snapshot(params, null);
            img=snapshot;
        }
        prex=-100;
        prey=-100;
        numcount[j]=i;
        j++;
    }
    @FXML
    private void leftPicture(ActionEvent e){
        gc.clearRect(0,0,600,400);
        mx=0;my=0;index=1.0;
        img=vbox.getLeftPicture();
        paraOfH=(double)400/(double)img.getHeight();
        paraOfW=(double)600/(double)img.getWidth();
        if(paraOfH>paraOfW)para=paraOfW;else para=paraOfH;
        gc.drawImage(img,(300-(int)(img.getWidth()*para*index)/2)+mx,(200-(int)(img.getHeight()*para*index)/2)+my,
                    ((int)(img.getWidth()*para*index))+mx/2,((int)(img.getHeight()*para*index))+my/2); 
    }
    @FXML
    private void rightPicture(ActionEvent e){
        gc.clearRect(0,0,600,400);
        mx=0;my=0;index=1.0;
        img=vbox.getRightPicture();
        paraOfH=(double)400/(double)img.getHeight();
        paraOfW=(double)600/(double)img.getWidth();
        if(paraOfH>paraOfW)para=paraOfW;else para=paraOfH;
        gc.drawImage(img,(300-(int)(img.getWidth()*para*index)/2)+mx,(200-(int)(img.getHeight()*para*index)/2)+my,
                    ((int)(img.getWidth()*para*index))+mx/2,((int)(img.getHeight()*para*index))+my/2); 
    }
}