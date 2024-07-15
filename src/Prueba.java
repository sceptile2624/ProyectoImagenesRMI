import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;



public class Prueba {
    private List<ImageProcessor> servidores;
    public static void main(String[] args) throws IOException, NotBoundException {
        //MULTIARCHIVO
        //Selecciona una capeta
        String rutaCarpeta = "C:\\Users\\ieliz\\Downloads\\archive\\10000";
        String rutaDestino = "C:\\Users\\ieliz\\Pictures\\Pruebas";

        //cargar imagenes en memoria
        List<BufferedImage> imagenesCargadas = FiltroConcurrente.cargarImagenesDesdeDirectorio(rutaCarpeta, 5);

        //convertir imagenes en byte[]
        List<byte[]> listaArregloBytes = new ArrayList<>();

        for(BufferedImage imagen: imagenesCargadas){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imagen, "jpg", baos);
            byte[] imagenBytes = baos.toByteArray();
            listaArregloBytes.add(imagenBytes);
        }

        List<ImageProcessor> servers = new ArrayList<>();
        servers.add((ImageProcessor) Naming.lookup("//localhost:1234/Imagenes1"));
        servers.add((ImageProcessor) Naming.lookup("//localhost:1235/Imagenes2"));
        servers.add((ImageProcessor) Naming.lookup("//192.168.100.2:1236/Imagenes3"));

        // Crear un pool de hilos
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Crear una lista para almacenar los futuros resultados
        List<Future<byte[]>> futures = new ArrayList<>();

        // Enviar y recibir imágenes en paralelo
        for (int i = 0; i < listaArregloBytes.size(); i++) {
            byte[] imgByte = listaArregloBytes.get(i);
            ImageProcessor server = servers.get(i % servers.size());
            Callable<byte[]> task = () -> {
                return server.applyFilter(imgByte);
            };
            futures.add(executor.submit(task));
        }

        // Apagar el executor
        executor.shutdown();

        // Recoger los resultados
        List<byte[]> listaFiltradaArregloBytes = new ArrayList<>();
        for (Future<byte[]> future : futures) {
            try {
                listaFiltradaArregloBytes.add(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //convertir byte a imagenes
        List<BufferedImage> imagenesFiltradas = new ArrayList<>();
        for(byte[] imgByte: listaFiltradaArregloBytes){
            ByteArrayInputStream bais = new ByteArrayInputStream(imgByte);
            imagenesFiltradas.add(ImageIO.read(bais));
        }

        //Guardar las imagenes
        FiltroConcurrente.guardarImagenes(imagenesFiltradas,rutaDestino,5);
    }
}
