package Util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class ClienteUDP {
    private JPanel JPPrincipal; // Panel principal que contiene todos los componentes
    private JTextField tfMensaje; // Campo de texto para escribir mensajes
    private JButton btnEnviar; // Botón para enviar mensajes
    private JTextPane taCliente; // JTextPane para mostrar los mensajes del cliente
    private JTextPane taServidor; // JTextPane para mostrar los mensajes del servidor
    private JLabel lblMensaje; // Etiqueta para indicar que se debe escribir un mensaje
    private JLabel lblMensaje2; // Etiqueta para indicar que se está conectado al servidor

    private static final String IP_SERVIDOR = "127.0.0.1"; // Dirección IP del servidor
    private static final int PUERTO_SERVIDOR = 12345; // Puerto del servidor

    public ClienteUDP() {
        // Generar un nombre de cliente aleatorio
        Random random = new Random();
        String nombreCliente = "Cliente" + random.nextInt(1000);

        // Acción del botón enviar
        btnEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Construir el mensaje con el nombre del cliente y el texto del campo de texto
                String mensaje = nombreCliente + ": " + tfMensaje.getText();
                enviarMensaje(mensaje); // Enviar el mensaje al servidor
                tfMensaje.setText(""); // Limpiar el campo de texto después de enviar el mensaje
                appendToCliente(mensaje); // Agregar el mensaje al JTextPane de la izquierda
            }
        });

        // Hilo para recibir mensajes del servidor
        Thread receiverThread = new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket(); // Usar un puerto aleatorio para el cliente

                while (true) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                    socket.receive(paquete); // Recibir datos del servidor

                    String recibido = new String(paquete.getData(), 0, paquete.getLength());
                    System.out.println("Mensaje recibido del servidor: " + recibido); // Mensaje de depuración
                    appendToServidor(recibido); // Agregar el mensaje al JTextPane de la derecha
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receiverThread.start(); // Iniciar el hilo para recibir mensajes del servidor
    }

    // Método para enviar mensajes al servidor
    private void enviarMensaje(String mensaje) {
        try {
            DatagramSocket socket = new DatagramSocket(); // Crear un nuevo socket UDP

            byte[] buffer = mensaje.getBytes();
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length,
                    InetAddress.getByName(IP_SERVIDOR), PUERTO_SERVIDOR);
            socket.send(paquete); // Enviar el mensaje al servidor
            System.out.println("Enviando mensaje al servidor: " + mensaje); // Mensaje de depuración

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para agregar mensajes al JTextPane del servidor
    private void appendToServidor(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            taServidor.setText(taServidor.getText() + mensaje + "\n"); // Agregar mensaje al JTextPane
            System.out.println("Texto actual del JTextPane taServidor: " + taServidor.getText()); // Mensaje de depuración
        });
        System.out.println("Texto actual del JTextPane taServidor: " + taServidor.getText()); // Mensaje de depuración
    }

    // Método para agregar mensajes al JTextPane del cliente
    private void appendToCliente(String mensaje) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                taCliente.setText(taCliente.getText() + mensaje + "\n"); // Agregar mensaje al JTextPane
            }
        });
    }

    // Método main para iniciar la aplicación
    public static void main(String[] args) {
        JFrame frame = new JFrame("Cliente UDP");
        frame.setContentPane(new ClienteUDP().JPPrincipal); // Establecer el panel principal en el frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // Ajustar el tamaño del frame automáticamente
        frame.setVisible(true); // Hacer visible el frame
    }
}
