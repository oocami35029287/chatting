import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.event.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.util.*;
import javafx.geometry.Insets;
import java.io.PrintWriter;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.io.File;
import javafx.geometry.Pos;
import java.lang.StringBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.*;
import java.util.logging.*;
import java.awt.Desktop;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.GraphicsContext;


public class ChattingController{
    // instance variables that refer to GUI components
    @FXML private TextArea TypeTextArea;
    @FXML private ScrollPane chattingPane;
    @FXML private Button sentButton;
    @FXML public  Button trigger;
    @FXML private Button backButton;
    @FXML public  Label nameLabel;
    @FXML private Button fileButton;
    // private instance
    public  ArrayList<String> Dialog=new ArrayList<String>();
    private AccessVBox root = new AccessVBox();
    private PrintWriter out;
    private Stage stage;
    private Scene scene2;
    public  String string="";
    public  File selectedFile;
    public String name;
    public boolean fileSentFlag=false;
    public String sentFileName;
    public boolean selectedFileFlag=false;
    public LoadWait loadwait;
    //handler
    @FXML
    private void sentButtonPressed(ActionEvent e){              //alt+enter sent
        sent();
    }
    
    @FXML
    private void enterPressed(KeyEvent e){                      //button sent
        if(e.getCode().equals(KeyCode.ENTER))
            sent();
    }
    
    @FXML
    private synchronized void triggerAction(ActionEvent e){
        
        if(!string.equals("")){                             //1.傳送文字
            AccessBox box = new AccessBox();
            Label msgLabel=new Label();
            ImageView picture = new ImageView();
            boolean pictureFlag=false;
            
            box.setPrefWidth(340);
            String n = string.substring(0,string.indexOf(":")+1);
            String st = string.substring(string.indexOf(":")+2);
            StringBuilder sb = new StringBuilder(st);       
            for(int i=0 ;i<st.length();i++){
                if(i%30==29)
                    sb.insert(i,"\n");
            }
            msgLabel.setText(sb.toString());
            
            
            if(string.startsWith(name)){                            //<<<<<自己>>>>>>>
                msgLabel.setStyle("-fx-background-color: white; -fx-padding: 6 6 6 6 ;-fx-background-radius: 3;"); 
                if(st.startsWith("FILE")){                          //檔案貼圖片
                    String sm;
                    if(selectedFileFlag==true){
                        sm=selectedFile.getName();
                        selectedFileFlag=false;
                    }
                    else sm=st.substring(4);
                    File f = new File("./storage/"+sm);
                    System.out.println("file: "+f.getName());       //第一次貼會有 \n 在結尾!?所以有bug
                    if(f.exists()){                             
                        pastPicture(box,"./storage/"+sm);
                    }
                }
                else
                    box.getChildren().add(msgLabel);//文字
                box.setAlignment(Pos.CENTER_RIGHT);
            }   
            else{                                                   //<<<<<對方>>>>>
                msgLabel.setStyle(" -fx-background-color: #DA70D6;-fx-padding: 6 6 6 6 ;-fx-background-radius: 3;"); 
                box.getChildren().add(new Label(n));                //對方名字
                if(st.startsWith("FILE")){                          //貼上檔案
                    File f = new File("./storage/"+st.substring(4));
                    if(f.exists()){
                        pastPicture(box,"./storage/"+st.substring(4));
                    }
                    
                }
                else {
                    box.getChildren().add(msgLabel);
                }
                box.setAlignment(Pos.CENTER_LEFT);
                 
            }
            
            addToChattingPane(box);
        }
        if(fileSentFlag){                                       //2.當圖片第一次傳過來時
            AccessBox box = new AccessBox();
            pastPicture(box,"./storage/"+sentFileName);
            box.setAlignment(Pos.CENTER_LEFT);
            addToChattingPane(box);
            
            sentFileName="";
            fileSentFlag=false;
        }        
    }
    private  void addToChattingPane(AccessBox box){                   //將box 貼到pane上 
        root.addBox(box);
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        chattingPane.setContent(root);  
        string="";
        chattingPane.setVvalue(1);
        loadwait.loadNotify();
    }
    private void pastPicture(AccessBox box,String fileName){                         //圖片判別之後加到box內
            if(fileName.substring(fileName.lastIndexOf('.') + 1).startsWith("jpg")||
                fileName.substring(fileName.lastIndexOf('.') + 1).startsWith("png")){
                
                
                box.addPicture(new Image(fileName));                //modify
                box.addEventFilter(
                    MouseEvent.MOUSE_CLICKED,eh->{
                        try{
                        Stage picStage = new Stage();
                        FXMLLoader picFxmlLoader = new FXMLLoader(getClass().getResource("PictureView.fxml"));
                        Parent picRoot = picFxmlLoader.load();
                        Scene picScene = new Scene(picRoot);
                        PictureViewController picControl = picFxmlLoader.getController();
                        //*******************************************
                        picControl.setPicture(root,box,box.getPicture(),picStage);
                        
                        picStage.setScene(picScene);
                        picStage.show();}catch(Exception ev){System.out.println("XXX");}
                    }
                );
            }
            else{
                box.addPicture(new Image("./icon/txt.png")); 
                box.addEventFilter(
                    MouseEvent.MOUSE_CLICKED,eh->{
                        //Program.launch(fileName);
                        //Program.findProgram(fileName.lastIndexOf('.') + 1)).execute(FileName);
                    }
                );
                
            }
    }
    
    public void sent(){                                         //sent method
        if(TypeTextArea.getText().startsWith("FILE")){//如果是file，先將檔案貼到自己的資料夾
            if (selectedFile != null) {
                new Thread(()->{
                        try{
                        File dest = new File("./storage/"+selectedFile.getName()); 
                        Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException ex) {}
                        File f = new File("./storage/"+selectedFile.getName());
                        if(f.exists()){
                            System.out.println("轉移成功");
                        }else{System.out.println("失敗");}
                    }
                ).start(); 
            }
        }
        out.println(TypeTextArea.getText());
        TypeTextArea.clear();
    }

    
    @FXML
    private void back(){
        out.println("DISLINK");
        
        stage.close();
        stage.setScene(scene2);
        stage.show();
    }
    @FXML 
    private void fileButtonPressed(){
         FileChooser fileChooser = new FileChooser();
         fileChooser.setTitle("Open Resource File");
         fileChooser.getExtensionFilters().addAll(
                 new ExtensionFilter("Text Files", "*.txt"),
                 new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                 new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                 new ExtensionFilter("All Files", "*.*"));
         selectedFile = fileChooser.showOpenDialog(stage);
          if (selectedFile != null) {
          TypeTextArea.setText("FILE"+selectedFile.getName());}
          selectedFileFlag=true;
         
    }
    public void setOut(PrintWriter out,Stage stage,Scene scene2){
        this.out=out;
        this.stage=stage;
        this.scene2=scene2;
    }
    
    public void printOnPane(String string){
        //chattingTextArea.appendText(string+"\n");  
    }
    public void clearChatRoom(){
        root.clear();
    }
    public void setLoadWait(LoadWait loadwait){
        this.loadwait=loadwait;
    }
    


}