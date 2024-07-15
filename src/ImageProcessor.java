import java.rmi.Remote;
import java.rmi.RemoteException;
import java.awt.image.BufferedImage;
import java.util.List;

public interface ImageProcessor extends Remote {
    byte[] applyFilter(byte[] imageBytes) throws RemoteException;
}
