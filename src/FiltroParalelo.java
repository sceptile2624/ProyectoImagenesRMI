import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FiltroParalelo {
    public static int aplicarFiltro(String rutaCarpeta, String rutaDestino, int numHilos, List<ImageProcessor> servidores){
        int totalImagenes = 0;
        try{
            System.out.println("Generando lista");
            List<BufferedImage> imagenes = cargarImagenesDesdeDirectorio(rutaCarpeta,numHilos);

            //Convertir imagenes en byte[]
            List<byte[]> listaArregloBytes = new ArrayList<>();

            for(BufferedImage imagen: imagenes){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(imagen, "jpg", baos);
                byte[] imagenBytes = baos.toByteArray();
                listaArregloBytes.add(imagenBytes);
            }
            // Crear un pool de hilos
            ExecutorService executor = Executors.newFixedThreadPool(numHilos);

            // Crear lista para almacenar los Future
            List<Future<byte[]>> futures = new ArrayList<>();

            System.out.println("Aplicando Filtro");
            // Enviar y recibir imágenes en paralelo
            for (int i = 0; i < listaArregloBytes.size(); i++) {
                byte[] imgByte = listaArregloBytes.get(i);
                ImageProcessor server = servidores.get(i % servidores.size());
                Callable<byte[]> task = () -> {
                    try {
                        return server.applyFilter(imgByte);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error al aplicar el filtro");
                        return null;
                    }
                };
                futures.add(executor.submit(task));
            }
            // Apagar el executor
            executor.shutdown();
            // Recoger los resultados
            List<byte[]> listaFiltradaArregloBytes = new ArrayList<>();
            for (Future<byte[]> future : futures) {
                try {
                    byte[] result = future.get();
                    if (result != null) {
                        listaFiltradaArregloBytes.add(result);
                    } else {
                        System.err.println("Error: Result is null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //convertir byte a imagenes
            List<BufferedImage> imagenesFiltradas = new ArrayList<>();
            for(byte[] imgByte: listaFiltradaArregloBytes){
//                ByteArrayInputStream bais = new ByteArrayInputStream(imgByte);
//                imagenesFiltradas.add(ImageIO.read(bais));
                ByteArrayInputStream bais = new ByteArrayInputStream(imgByte);
                BufferedImage img = ImageIO.read(bais);
                if (img != null) {
                    imagenesFiltradas.add(img);
                } else {
                    System.err.println("Error: Filtered image is null");
                }
            }



            System.out.println("Guardando Lista");
            guardarImagenes(imagenesFiltradas,rutaDestino,numHilos);
            totalImagenes = imagenesFiltradas.size();
        }catch (Exception e){e.printStackTrace();}
        return totalImagenes;
    }

    public static List<BufferedImage> cargarImagenesDesdeDirectorio(String rutaCarpeta, int numHilos) {
        System.out.println("Cargando imagenes desde el directorio");
        List<BufferedImage> imagenes = new ArrayList<>();
        File directorio = new File(rutaCarpeta);

        if (directorio.exists() && directorio.isDirectory()) {
            ExecutorService executor = Executors.newFixedThreadPool(numHilos);
            cargarImagenes(directorio, imagenes, executor);
            executor.shutdown();
            while (!executor.isTerminated()) {}
        }
        return imagenes;
    }
    private static void cargarImagenes(File directorio, List<BufferedImage> imagenes, ExecutorService executor){
        System.out.println("Entro al metodo cargarImagenes");
        File[] listaArchivos = directorio.listFiles();
        if(listaArchivos != null){
            for(File archivo : listaArchivos){
                if(archivo.isDirectory()){
                    cargarImagenes(archivo,imagenes, executor);
                }else{
                    if(isImageFile(archivo)){
                        executor.execute(()->{
                            try{
                                BufferedImage image = ImageIO.read(archivo);
                                if(image != null){
                                    imagenes.add(image);
                                }
                            }catch (IOException e){e.printStackTrace();}
                        });
                    }
                }
            }
        }
    }
    private static boolean isImageFile(File file) {
        String nombre = file.getName().toLowerCase();
        return nombre.endsWith(".jpg") || nombre.endsWith(".jpeg") || nombre.endsWith(".png") || nombre.endsWith(".gif")
                || nombre.endsWith(".webp");
    }
    public static void guardarImagenes(List<BufferedImage> listaImagenes, String rutaDestino, int numHilos){
        ExecutorService executor = Executors.newFixedThreadPool(numHilos);

        for(int i = 0; i < listaImagenes.size(); i++){
            final int index = i;
            executor.execute(() -> guardarImagen(listaImagenes.get(index), rutaDestino, index+1));
        }

        executor.shutdown();
        while(!executor.isTerminated()){}
    }
    private static void guardarImagen(BufferedImage imagen, String rutaDestino, int numeroImagen) {
        String nombreArchivo = "imagen_" + numeroImagen + ".jpg";
        String rutaArchivo = rutaDestino + File.separator + nombreArchivo;
        try {
            File archivoSalida = new File(rutaArchivo);
            ImageIO.write(imagen, "jpg", archivoSalida);
        } catch (IOException e) {
            System.err.println("Error al guardar la imagen en: " + rutaArchivo);
            e.printStackTrace();
        }
    }

    private static void agregarTexto(String valores){
        String texto = Ventana.txtEstadosResultadosHilos.getText();;
        texto += valores;
        Ventana.txtEstadosResultadosHilos.setText(texto);

    }
}
