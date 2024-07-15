
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FiltroConcurrente {

    public static int aplicarFiltroMultiArchivo(String rutaCarpeta, String rutaDestino, int numHilos) {
        System.out.println("Generando Lista");
        List<BufferedImage> imagenes = cargarImagenesDesdeDirectorio(rutaCarpeta, numHilos);

        System.out.println("Aplicando Filtro");
        List<BufferedImage> imagenesFiltradas = aplicarFiltroConcurrente(imagenes,numHilos);

        System.out.println("Guardando Lista");
        guardarImagenes(imagenesFiltradas, rutaDestino, numHilos);
        return imagenesFiltradas.size();
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
    private static List<BufferedImage> aplicarFiltroConcurrente(List<BufferedImage> imagenes, int numHilos){
        ExecutorService executor = Executors.newFixedThreadPool(numHilos);
        List<BufferedImage> listaImagenesFiltradas = new ArrayList<>();

        imagenes.forEach(imagen -> executor.execute(()->{
            BufferedImage imagenFiltrada = aplicarFiltro(imagen);
            synchronized (listaImagenesFiltradas){
                listaImagenesFiltradas.add(imagenFiltrada);
                agregarTexto("El hilo: " + Thread.currentThread().getName() + " ha filtrado una imagen\n");
            }
        }));
        executor.shutdown();
        while(!executor.isTerminated()){}

        return listaImagenesFiltradas;
    }
    private static BufferedImage aplicarFiltro(BufferedImage imagen) {
        int width = imagen.getWidth();
        int height = imagen.getHeight();
        BufferedImage imagenFiltrada = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Ajustes de contraste y saturación
        float contrastFactor = 0.5f;
        float saturationFactor = 0.5f;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = imagen.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Ajuste de contraste
                red = (int) ((red - 128) * contrastFactor + 128);
                green = (int) ((green - 128) * contrastFactor + 128);
                blue = (int) ((blue - 128) * contrastFactor + 128);

                // Ajuste de saturación
                int gray = (red + green + blue) / 3;
                red = (int) (red + saturationFactor * (gray - red));
                green = (int) (green + saturationFactor * (gray - green));
                blue = (int) (blue + saturationFactor * (gray - blue));

                red = Math.min(255, Math.max(0, red));
                green = Math.min(255, Math.max(0, green));
                blue = Math.min(255, Math.max(0, blue));

                int newRGB = (red << 16) | (green << 8) | blue;
                imagenFiltrada.setRGB(x, y, newRGB);
            }
        }
        return imagenFiltrada;
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
