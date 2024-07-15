package Clases;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Filtro {
    public static int aplicarFiltro(String rutaImg, String rutaDestino){
        List<BufferedImage> listaImagen = new ArrayList<>();
        File archivo = new File(rutaImg);
        if(archivo.isFile()){
            try{
                BufferedImage image = ImageIO.read(archivo);
                if(image != null){
                    listaImagen.add(image);
                }
            }catch (IOException e){e.printStackTrace();}
        }

        List<BufferedImage> imagenFiltrada = new ArrayList<>();
        for(BufferedImage image: listaImagen){
            imagenFiltrada.add(filtroLogaritmico(image));
        }

        guardarImagenes(imagenFiltrada, rutaDestino);
        return imagenFiltrada.size();
    }
    public static int  aplicarFiltroMultiArchivo(String rutaCarpeta, String rutaDestino){
        //generarListaDeImagenes
        List<BufferedImage> listaImagenes = cargarImagenesDesdeDirectorio(rutaCarpeta);

        //Aplicarles el filtro
        List<BufferedImage> listaFiltrada = new ArrayList<>();
        for(BufferedImage imagen : listaImagenes){
            listaFiltrada.add(filtroLogaritmico(imagen));
        }

        //Guardarlas en el destino
        guardarImagenes(listaFiltrada, rutaDestino);

        return listaImagenes.size();
    }
    private static void guardarImagenes(List<BufferedImage> listaImagenesFiltradas, String rutaDestino){
        File directorioDestino = new File(rutaDestino);
        if(!directorioDestino.exists()){
            directorioDestino.mkdirs();
        }

        for(int i = 0; i < listaImagenesFiltradas.size(); i++){
            BufferedImage imagen = listaImagenesFiltradas.get(i);
            String nombreArchivo = "imagen_" + (i+1) + ".jpg";
            String rutaArchivo = rutaDestino + File.separator + nombreArchivo;
            try{
                File archivoSalida = new File(rutaArchivo);
                ImageIO.write(imagen, "jpg", archivoSalida);
            }catch (IOException e){
                System.err.println("Error al guardar la imagen en: "+rutaArchivo);
                e.printStackTrace();
            }
        }
    }

    private static BufferedImage filtroLogaritmico(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Ajuste de contraste
        float contrastFactor = 0.5f; // Factor de contraste ajustable (0 para eliminar el contraste)
        // Ajuste de saturación
        float saturationFactor = 0.5f; // Factor de saturación ajustable (0 para eliminar la saturación)

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Aplicar ajuste de contraste
                red = (int) ((red - 128) * contrastFactor + 128);
                green = (int) ((green - 128) * contrastFactor + 128);
                blue = (int) ((blue - 128) * contrastFactor + 128);

                // Aplicar ajuste de saturación
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

    //Cargar Imagen
    private  static List<BufferedImage> cargarImagenesDesdeDirectorio(String url){
        List<BufferedImage> imagenes = new ArrayList<>();
        File directorio =  new File(url);
        if(directorio.exists() && directorio.isDirectory()){
            buscarImagen(directorio,imagenes);
        }

        return imagenes;
    }
    private static void buscarImagen(File directorio, List<BufferedImage> imagenes){
        File[] ListaArchivos = directorio.listFiles();
        if(ListaArchivos != null){
            for(File archivo : ListaArchivos){
                if(archivo.isDirectory()){
                    buscarImagen(archivo,imagenes);
                }else {
                    if (isImageFile(archivo)) {
                        try {
                            BufferedImage image = ImageIO.read(archivo);
                            if (image != null) {
                                imagenes.add(image);
                            }
                        } catch (IOException e) {e.printStackTrace();}
                    }
                }
            }
        }
    }
    private static boolean isImageFile(File file){
        String nombre = file.getName().toLowerCase();
        return nombre.endsWith(".jpg") || nombre.endsWith(".jpeg") || nombre.endsWith(".png") || nombre.endsWith(".gif") || nombre.endsWith(".webp");
    }
}
