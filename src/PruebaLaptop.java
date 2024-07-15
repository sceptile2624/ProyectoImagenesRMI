import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.awt.image.BufferedImage;

public class PruebaLaptop extends UnicastRemoteObject implements ImageProcessor {

    protected PruebaLaptop() throws RemoteException {
        super();
    }

    @Override
    public byte[] applyFilter(byte[] imageBytes) throws RemoteException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bais);
            BufferedImage filteredImage = filtroLogaritmico(image);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(filteredImage, "jpg", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Error processing image", e);
        }
    }

    private static BufferedImage filtroLogaritmico(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        float contrastFactor = 0.5f;
        float saturationFactor = 0.5f;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                red = (int) ((red - 128) * contrastFactor + 128);
                green = (int) ((green - 128) * contrastFactor + 128);
                blue = (int) ((blue - 128) * contrastFactor + 128);

                int gray = (red + green + blue) / 3;
                red = (int) (red + saturationFactor * (gray - red));
                green = (int) (green + saturationFactor * (gray - green));
                blue = (int) (blue + saturationFactor * (gray - blue));

                red = Math.min(255, Math.max(0, red));
                green = Math.min(255, Math.max(0, green));
                blue = Math.min(255, Math.max(0, blue));

                int newRGB = (red << 16) | (green << 8) | blue;
                resultImage.setRGB(x, y, newRGB);
            }
        }

        return resultImage;
    }

    public static void main(String[] args) {
        try {
            int puerto = Integer.parseInt(JOptionPane.showInputDialog("puerto: "));
            String objetoRemoto = JOptionPane.showInputDialog("Nombre del objeto Remoto: ");

            LocateRegistry.createRegistry(puerto);

            PruebaLaptop obj = new PruebaLaptop();
            Naming.rebind("//"+ InetAddress.getLocalHost().getHostAddress() +":"+ puerto +"/"+objetoRemoto, obj);
            System.out.println("Servidor Listo en el puerto: "+puerto);
            System.out.println("Nombre del objeto: "+objetoRemoto);
            String servidorListo = String.format(
                    "Servidor listo.\nPuerto: %d\nObjeto Remoto: %s",
                    puerto,
                    objetoRemoto
            );
            JOptionPane.showMessageDialog(null,servidorListo);
        } catch (Exception e) {
            System.err.println("ImageProcessorImpl exception:");
            e.printStackTrace();
        }
    }
}
