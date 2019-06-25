import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.PrintWriter;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
public class Friend extends VBox{
    private String chatRoomName; 
    private String chatRoomID;
    private String members="";
    
    public Friend(String chatRoomName, String chatRoomID) {
        this.chatRoomName = chatRoomName;
        this.chatRoomID = chatRoomID;
        Label l = new Label(chatRoomName);
        this.setStyle("-fx-background-color: #DDA0DD;-fx-padding: 6 6 6 6 ;-fx-background-radius: 3;-fx-pref-width: 382;-fx-pref-height: 50");
        l.setStyle(" -fx-font-size:16;");
        this.getChildren().add(l);
        this.setAlignment(Pos.CENTER);
        
        
    }
    public Friend(String chatRoomName,String members,String chatRoomID) {
        this.chatRoomName = chatRoomName;
        this.chatRoomID = chatRoomID;
        this.members=members;
        Label l = new Label(chatRoomName);
        this.setStyle("-fx-background-color: #DDA0DD;-fx-padding: 6 6 6 6 ;-fx-background-radius: 3;-fx-pref-width: 382;");
        l.setStyle(" -fx-font-size:16;");
        this.getChildren().add(l);
        Label l2 = new Label(members);
        this.setStyle("-fx-background-color: #DDA0DD;-fx-padding: 6 6 6 6 ;-fx-background-radius: 3;-fx-pref-width: 382;");
        l2.setStyle(" -fx-font-size:10; -fx-font-color: #708090");
        this.getChildren().add(l2);
        this.setAlignment(Pos.CENTER);
        
        
    }
    public String getChatRoomName(){
        return chatRoomName;
    }
    public String getChatRoomID(){
        return chatRoomID;
    }
}