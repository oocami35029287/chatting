import java.lang.InterruptedException;

public class LoadWait{
    private boolean stop=false;
    public void loadWaitting(){
        while(stop){
            try{Thread.sleep(10);}catch(InterruptedException e){};
        }
    }
    public void loadNotify(){
        stop=false;
    }
    public void loadWait(){
        stop=true;
    }
}