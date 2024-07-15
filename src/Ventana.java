import Clases.Filtro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class Ventana extends JFrame {
    //INSTANCIAS
    //SELECCION DE CARPETAS
    JButton btnSeleccionarCarpeta;
    JButton btnSeleccionarCarpetaDestino;
    JTextField jtfRutaEntradaCarpeta;
    JTextField jtfRutaDestino;

    //SECUENCIAL
    JButton btnSecuencial;
    JLabel txtResultadosSecuencial;
    JLabel txtElementosSecuencial;
    JLabel txtElementosResultadoSecuencial;
    JLabel txtTiempoSecuencial;
    JLabel txtTiempoResultadoSecuencial;
    JLabel txtEstadoSecuencial;
    JLabel txtEstadoResultadoSecuencial;
    JLabel estadoProcesoSecuencial;

    //CONCURRENTE
    JButton btnConcurrente;
    JTextField jtfNumeroHilosConcurrente;
    JLabel txtNumeroHilosConcurrente;
    JLabel txtResultadosConcurrente;
    JLabel txtelementosConcurrente;
    JLabel txtElementosResultadoConcurrente;
    JLabel txtTiempoConcurrente;
    JLabel txtTiempoResultadoConcurrente;
    JLabel txtEstadoConcurrente;
    JLabel txtEstadoResultadoConcurrente;
    JLabel estadoProcesoConcurrente;

    //PARALELO
    JButton btnParalelo;
    JLabel txtNumeroHilosParalelo;
    JTextField jtfNumeroHilosParalelo;
    JLabel txtResultadoParalelo;
    JLabel txtClientesParalelo;
    JLabel txtClientesResultadoParalelo;
    JLabel txtElementosParalelo;
    JLabel txtElementosResultadoParalelo;
    JLabel txtTiempoParalelo;
    JLabel txtTiempoResultadoParalelo;
    JLabel txtEstadoParalelo;
    JLabel txtEstadoResultadoParalelo;
    JLabel estadoProcesoParalelo;

    JButton btnEliminarClienteParalelo;

    //CLIENTES
    JLabel txtTituloClientes;
    JButton btnAgregarClienteParalelo;
    JTextArea jtaClientesParalelo;
    private int numeroClientes;


    public static JTextArea txtEstadosResultadosHilos;

    private String rutaCarpeta;
    private String rutaDestino;
    private List<ImageProcessor> servidores;


    public Ventana() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            componentesVentana();
            Componentes();
            inicializarEventos();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void componentesVentana(){
        setLayout(null);
        setTitle("Filtro Logaritmico");
        setSize(1080, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    public void Componentes(){
        //Inicializar Componentes

        //CARPETAS
        rutaDestino = "";
        rutaCarpeta = "";
        numeroClientes = 0;
        btnSeleccionarCarpeta = new JButton("Seleccionar Carpeta");
        btnSeleccionarCarpetaDestino = new JButton("Seleccionar carpeta destino");
        jtfRutaEntradaCarpeta = new JTextField();
        jtfRutaDestino = new JTextField();


        //SECUENCIAL
        btnSecuencial = new JButton("Secuencial");
        txtResultadosSecuencial = new JLabel("RESULTADOS.");
        txtElementosSecuencial = new JLabel("Total de elementos: ");
        txtElementosResultadoSecuencial = new JLabel("0");
        txtTiempoSecuencial = new JLabel("Tiempo Total: ");
        txtTiempoResultadoSecuencial = new JLabel("0 ms");
        txtEstadoSecuencial = new JLabel("Estado: ");
        txtEstadoResultadoSecuencial = new JLabel(".");
        estadoProcesoSecuencial = new JLabel("No Inicializado");

        //CONCURRENTE
        btnConcurrente = new JButton("Concurrente");
        txtNumeroHilosConcurrente = new JLabel("Numero de Hilos");
        jtfNumeroHilosConcurrente = new JTextField("1");
        txtResultadosConcurrente = new JLabel("RESULTADOS.");
        txtelementosConcurrente = new JLabel("Total de elementos: ");
        txtElementosResultadoConcurrente = new JLabel("0");
        txtTiempoConcurrente = new JLabel("Tiempo Total: ");
        txtTiempoResultadoConcurrente = new JLabel("0 ms");
        txtEstadoConcurrente = new JLabel("Estado: ");
        txtEstadoResultadoConcurrente = new JLabel(".");
        estadoProcesoConcurrente = new JLabel("No Inicializado");
        txtEstadosResultadosHilos = new JTextArea();

        //PARALELO
        btnParalelo = new JButton("Paralelo");
        txtNumeroHilosParalelo = new JLabel("Numero de hilos: ");
        jtfNumeroHilosParalelo = new JTextField("1");
        txtResultadoParalelo = new JLabel("RESULTADOS:");
        txtClientesParalelo = new JLabel("Total clientes:");
        txtClientesResultadoParalelo = new JLabel("0");
        txtElementosParalelo = new JLabel("Total de elementos: ");
        txtElementosResultadoParalelo = new JLabel("0");
        txtTiempoParalelo = new JLabel("Tiempo total: ");
        txtTiempoResultadoParalelo = new JLabel("0 ms");
        txtEstadoParalelo = new JLabel("Estado: ");
        txtEstadoResultadoParalelo = new JLabel(".");
        estadoProcesoParalelo = new JLabel("No inicializado");

        //CLIENTES
        txtTituloClientes = new JLabel("Clientes");
        btnAgregarClienteParalelo = new JButton("Agregar Cliente");
        jtaClientesParalelo = new JTextArea("Lista de Clientes.");
        servidores = new ArrayList<>();
        btnEliminarClienteParalelo = new JButton("Eliminar Cliente");

        //CONFIGURAR COMPONENTES
        jtfRutaEntradaCarpeta.setEnabled(false);
        jtfRutaDestino.setEnabled(false);
        jtaClientesParalelo.setEnabled(false);
        txtClientesResultadoParalelo.setText(String.valueOf(numeroClientes));

//        btnParalelo.setEnabled(false);
//        btnConcurrente.setEnabled(false);

        setColorEstado(estadoProcesoSecuencial, Color.lightGray);
        setColorEstado(estadoProcesoConcurrente, Color.lightGray);

        //POSICION COMPONENTES
        //Carpeta
        btnSeleccionarCarpeta.setBounds(15,15,240,25);
        btnSeleccionarCarpetaDestino.setBounds(15,45,240,25);
        jtfRutaEntradaCarpeta.setBounds(265,15,450,25);
        jtfRutaDestino.setBounds(265,45,450,25);

        //Secuencial
        btnSecuencial.setBounds(15,100,225,35);
        txtResultadosSecuencial.setBounds(15,150,100,15);
        txtElementosSecuencial.setBounds(15,165,100,15);
        txtTiempoSecuencial.setBounds(15,185,100,15);
        txtEstadoSecuencial.setBounds(15,200,100,15);
        txtElementosResultadoSecuencial.setBounds(120,165,100,15);
        txtTiempoResultadoSecuencial.setBounds(120,185,100,15);
        txtEstadoResultadoSecuencial.setBounds(120,200,100,15);
        estadoProcesoSecuencial.setBounds(15,220,100,15);

        //Concurrente
        btnConcurrente.setBounds(250,100,225,35);
        txtNumeroHilosConcurrente.setBounds(250,150,100,15);
        jtfNumeroHilosConcurrente.setBounds(350,150,100,15);
        txtResultadosConcurrente.setBounds(250,175,100,15);
        txtelementosConcurrente.setBounds(250,195,100,15);
        txtTiempoConcurrente.setBounds(250,215,100,15);
        txtEstadoConcurrente.setBounds(250,235,100,15);
        txtElementosResultadoConcurrente.setBounds(350,195,100,15);
        txtTiempoResultadoConcurrente.setBounds(350,215,100,15);
        txtEstadoResultadoConcurrente.setBounds(350,235,100,15);

        //Paralelo
        btnParalelo.setBounds(500,100,225,35);
        txtNumeroHilosParalelo.setBounds(500,150,100,15);
        jtfNumeroHilosParalelo.setBounds(600,150,100,15);
        txtResultadoParalelo.setBounds(500,175,100,15);
        txtClientesParalelo.setBounds(500,195,100,15);
        txtElementosParalelo.setBounds(500,215,100,15);
        txtTiempoParalelo.setBounds(500,235,100,15);
        txtEstadoParalelo.setBounds(500,255,100,15);
        txtClientesResultadoParalelo.setBounds(600,195,100,15);
        txtElementosResultadoParalelo.setBounds(600,215,100,15);
        txtTiempoResultadoParalelo.setBounds(600,235,100,15);
        txtEstadoResultadoParalelo.setBounds(600,255,100,15);


        txtEstadosResultadosHilos.setBounds(250,300,400,200);

        //Clientes
        txtTituloClientes.setBounds(750,15,300,25);
        btnAgregarClienteParalelo.setBounds(750,45,300,25);
        btnEliminarClienteParalelo.setBounds(750,75,300,25);
        jtaClientesParalelo.setBounds(750,100,300,350);


        //Añadir
        add(estadoProcesoSecuencial);
        add(estadoProcesoConcurrente);

        add(btnSeleccionarCarpeta);
        add(btnSeleccionarCarpetaDestino);
        add(jtfRutaEntradaCarpeta);
        add(jtfRutaDestino);

        add(btnSecuencial);
        add(txtResultadosSecuencial);
        add(txtElementosSecuencial);
        add(txtElementosResultadoSecuencial);
        add(txtTiempoSecuencial);
        add(txtTiempoResultadoSecuencial);
        add(txtEstadoSecuencial);
        add(txtEstadoResultadoSecuencial);

        add(btnConcurrente);
        add(jtfNumeroHilosConcurrente);
        add(txtNumeroHilosConcurrente);
        add(txtResultadosConcurrente);
        add(txtelementosConcurrente);
        add(txtElementosResultadoConcurrente);
        add(txtTiempoConcurrente);
        add(txtTiempoResultadoConcurrente);
        add(txtEstadoConcurrente);
        add(txtEstadoResultadoConcurrente);
        add(txtEstadosResultadosHilos);

        add(btnParalelo);
        add(txtNumeroHilosParalelo);
        add(jtfNumeroHilosParalelo);
        add(txtResultadoParalelo);
        add(txtClientesParalelo);
        add(txtClientesResultadoParalelo);
        add(txtElementosParalelo);
        add(txtElementosResultadoParalelo);
        add(txtTiempoParalelo);
        add(txtTiempoResultadoParalelo);
        add(txtEstadoParalelo);
        add(txtEstadoResultadoParalelo);
        add(estadoProcesoParalelo);

        add(txtTituloClientes);
        add(btnAgregarClienteParalelo);
//        add(btnEliminarClienteParalelo);
        add(jtaClientesParalelo);
    }
    public void inicializarEventos(){
        //SELECCIONAR CARPETA
        btnSeleccionarCarpeta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { seleccionarCarpeta();}
        });

        //SELECCIONAR CARPETA DESTINO
        btnSeleccionarCarpetaDestino.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {seleccionarCarpetaDestino();}
        });

        //BOTON CONVERTIR SECUENCIAL
        btnSecuencial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarEstadoSecuencial(false);
                metodoSecuencial();

            }
        });

        //BOTON CONVERTIR CONCURRENTE
        btnConcurrente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarEstadoConcurrente(false);
                metodoConcurrente();

            }
        });
        //BOTON PARALELA
        btnParalelo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                metodoParalelo();
            }
        });
        //BOTON AGREGAR CLIENTE
        btnAgregarClienteParalelo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    agregarCliente();
                } catch (MalformedURLException | NotBoundException | RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        //BOTON ELIMINAR CLIENTE
        btnEliminarClienteParalelo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    System.out.println("Proximamente...");;
                }catch (Exception ex){ex.printStackTrace();}
            }
        });
    }

    public void seleccionarCarpeta(){
        //se crea el fileChooser y si es valido lo seleccionado se bloquea la seleccion individual
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(Ventana.this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            String ruta = selectedFolder.getAbsolutePath();
            jtfRutaEntradaCarpeta.setText(ruta); //muestra en pantalla la ruta seleccionada
            rutaCarpeta = ruta;//Guarda en variable la ruta
            System.out.println("Carpeta Origen: "+ rutaCarpeta);
        }else{
            JOptionPane.showMessageDialog(this,"Error al seleccionar carpeta.");
            rutaCarpeta = "1080";
        }

    }

    public void seleccionarCarpetaDestino(){
        JFileChooser fileChooser = new JFileChooser(new File("."));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(Ventana.this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            String ruta = selectedFolder.getAbsolutePath();
            jtfRutaDestino.setText(ruta);
            rutaDestino = ruta;
            System.out.println("Carpeta Destino:" + rutaDestino);
        }else{
            JOptionPane.showMessageDialog(this,"Error al seleccionar carpeta.");
            rutaDestino = "";
        }

    }
    public void metodoSecuencial(){
        //Tiene dos opciones; 1 archivo o multiArchivo
        if(jtfRutaEntradaCarpeta.getText().isEmpty() || jtfRutaDestino.getText().isEmpty()){
            JOptionPane.showMessageDialog(this,"Selecciona una carpeta de entrada y destino");
        }else{
            System.out.println("Multi archivos seleccionado");
            long tiempoInicial = System.nanoTime();
            int totalElementos = Filtro.aplicarFiltroMultiArchivo(rutaCarpeta,rutaDestino);
            long tiempoFinal = System.nanoTime();
            cambiarEstadoSecuencial(true);
            txtElementosResultadoSecuencial.setText(String.valueOf(totalElementos));
            txtTiempoResultadoSecuencial.setText(String.valueOf((tiempoFinal-tiempoInicial)/1000000) + " ms");
        }
    }
    public void metodoConcurrente(){
        if(jtfRutaEntradaCarpeta.getText().isEmpty() || jtfRutaDestino.getText().isEmpty()){
            JOptionPane.showMessageDialog(this,"Selecciona una carpeta de entrada y destino");
        }else if(btnSeleccionarCarpeta.isEnabled()){
            System.out.println("Metodo concurrente multiArchivo");
            txtEstadoResultadoConcurrente.setText("En proceso...");
            long tiempoInicial = System.nanoTime();
            int totalElementos = FiltroConcurrente.aplicarFiltroMultiArchivo(rutaCarpeta,rutaDestino, Integer.parseInt(jtfNumeroHilosConcurrente.getText())); //FiltroMultiConcurrente
            long tiempoFinal = System.nanoTime();
            cambiarEstadoConcurrente(true);
            txtElementosResultadoConcurrente.setText(String.valueOf(totalElementos));
            txtTiempoResultadoConcurrente.setText(((tiempoFinal-tiempoInicial)/1000000)+" ms");
            txtEstadoResultadoConcurrente.setText("Finalizado");
        }
    }
    public void metodoParalelo(){
        if(jtfRutaEntradaCarpeta.getText().isEmpty() || jtfRutaDestino.getText().isEmpty()){
        }else{
            int numeroClientes = Integer.parseInt(txtClientesResultadoParalelo.getText());
            int numeroHilo = Integer.parseInt(jtfNumeroHilosParalelo.getText());

            if (numeroClientes >= 1 && numeroHilo >= 1) {
                System.out.println("Metodo paralelo");
                txtEstadoResultadoParalelo.setText("En processo");
                long tiempoInicial = System.nanoTime();
                int totalElementos = FiltroParalelo.aplicarFiltro(rutaCarpeta, rutaDestino, numeroHilo, servidores);
                long tiempoFinal = System.nanoTime();
                txtElementosResultadoParalelo.setText(String.valueOf(totalElementos));
                txtTiempoResultadoParalelo.setText((((tiempoFinal-tiempoInicial)/1000000))+" ms");
                txtEstadoResultadoParalelo.setText("Finalizado");
            } else {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una cantidad de hilos y tener al menos un cliente.");
            }
        }
    }



    public void setColorEstado(JLabel label, Color color) {
        label.setOpaque(true);
        label.setBackground(color);
        label.setForeground(Color.WHITE); // Asegurar que el texto sea visible
    }

    public void cambiarEstadoSecuencial(boolean enProceso) {
        if (enProceso) {
            estadoProcesoSecuencial.setText("En proceso");
            setColorEstado(estadoProcesoSecuencial, Color.BLUE);
        } else {
            estadoProcesoSecuencial.setText("Finalizado");
            setColorEstado(estadoProcesoSecuencial, Color.GREEN);
        }
    }

    public void cambiarEstadoConcurrente(boolean enProceso) {
        if (enProceso) {
            estadoProcesoConcurrente.setText("En proceso");
            setColorEstado(estadoProcesoConcurrente, Color.BLUE);
        } else {
            estadoProcesoConcurrente.setText("Finalizado");
            setColorEstado(estadoProcesoConcurrente, Color.GREEN);
        }
    }

    public void agregarCliente() throws MalformedURLException, NotBoundException, RemoteException {
        String ip = JOptionPane.showInputDialog("Ingresa la IP del servidor:");
        String puerto = JOptionPane.showInputDialog("Ingresa el puerto del servidor:");
        String paquete = JOptionPane.showInputDialog("Ingresa el nombre del paquete del servidor:");

        if (ip != null && puerto != null && paquete != null && !ip.isEmpty() && !puerto.isEmpty() && !paquete.isEmpty()) {
            String ruta = "//" + ip + ":" + puerto + "/" + paquete;
            try {
                ImageProcessor servidor = (ImageProcessor) Naming.lookup(ruta);
                servidores.add(servidor);
                jtaClientesParalelo.append("\nCliente.\nIP: " + ip + "\nPuerto: " + puerto + "\nPaquete: " + paquete + "\n\n");

                numeroClientes += 1;
                txtClientesResultadoParalelo.setText(String.valueOf(numeroClientes));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al agregar el servidor: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, proporciona valores válidos para IP, puerto y nombre del paquete.");
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(()->{
            Ventana v = new Ventana();
            v.setVisible(true);
        });
    }
}
