import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import javafx.scene.control.Button;
public class AccessVBox extends VBox{
    private ArrayList<AccessBox> pictureList = new ArrayList<AccessBox>();
    private int picIndex=0;
    private int currentpicIndex;
    private Button left;
    private Button right;
    
    public void addBox(AccessBox box){
        if(box.isImageBox()){
            box.setIndex(picIndex);
            picIndex++;
            pictureList.add(box);
        }
        
        this.getChildren().add(box);
        //System.out.println("cur ind. "+picIndex);
        
    }
    public void setCurrentIndex(AccessBox box){
        currentpicIndex=box.getIndex();
    }
    public Image getLeftPicture(){
        if(currentpicIndex>0){
            currentpicIndex--;
            right.setStyle("-fx-text-fill: black;");
        }
        if(currentpicIndex==0){
            System.out.println("no picture");
            left.setStyle("-fx-text-fill: gray;");
        }
        System.out.println("current ind. "+currentpicIndex);
        return pictureList.get(currentpicIndex).getPicture();
    }
    public Image getRightPicture(){
        if(currentpicIndex<picIndex-1){
            currentpicIndex++;
            left.setStyle("-fx-text-fill: black;");
        }
        if(currentpicIndex==picIndex-1){
            System.out.println("no picture");
            right.setStyle("-fx-text-fill: gray;");
        }
        System.out.println("current ind. "+currentpicIndex);
        return pictureList.get(currentpicIndex).getPicture();
    }
    
    public void clear(){
        this.getChildren().clear();
        picIndex=0;
        pictureList.clear();
    }
    public void setButton(Button left,Button right){
        this.left=left;
        this.right=right;
    }
} 
                