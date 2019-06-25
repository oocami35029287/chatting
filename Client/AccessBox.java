import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class AccessBox extends HBox{
    private Image image;
    private int index=0;
    private boolean isImage=false;
    public void addPicture(Image image){
        this.image=image;
        ImageView picture = new ImageView(image);  
        this.getChildren().add(picture);
        double para;
        double paraOfH=(double)150/(double)image.getHeight();
        double paraOfW=(double)150/(double)image.getWidth();
        if(paraOfH>paraOfW)para=paraOfW;else para=paraOfH;
        picture.setFitHeight(image.getHeight()*para);picture.setFitWidth(image.getWidth()*para);
        isImage=true;
    }
    public  Image getPicture(){
        return image;
    }
    public void setIndex(int index){
        this.index=index;
    }
    public int getIndex(){
        return index;
    }
    public boolean isImageBox(){
        return isImage;
    }
} 
                