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
    private JPanel JPPrincipal;
    private JTextField tfMensaje;
    private JButton btnEnviar;
    private JTextPane taCliente;
    private JTextPane taServidor;
    private JLabel lblMensaje;
    private JLabel lblMensaje2;

    private static final String IP_SERVIDOR = "127.0.0.1";
    private static final int PUERTO_SERVIDOR = 12345; // Puerto del servidor

    public ClienteUDP() {
        // Generar un nombre de cliente aleatorio
        Random random = new Random();
        String nombreCliente = "Cliente" + random.nextInt(1000);



        btnEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mensaje = nombreCliente + ": " + tfMensaje.getText();
                enviarMensaje(mensaje);
                tfMensaje.setText("");
                appendToCliente(mensaje); // Agregar mensaje enviado al JTextPane de la izquierda
            }
        });

        Thread receiverThread = new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket(); // Usar un puerto aleatorio para el cliente

                while (true) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                    socket.receive(paquete);

                    String recibido = new String(paquete.getData(), 0, paquete.getLength());
                    System.out.println("Mensaje recibido del servidor: " + recibido); // Mensaje de depuración
                    appendToServidor(recibido); // Agregar mensaje recibido al JTextPane de la derecha
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receiverThread.start();
    }

    private void enviarMensaje(String mensaje) {
        try {
            DatagramSocket socket = new DatagramSocket();

            byte[] buffer = mensaje.getBytes();
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length,
                    InetAddress.getByName(IP_SERVIDOR), PUERTO_SERVIDOR);
            socket.send(paquete);
            System.out.println("Enviando mensaje al servidor: " + mensaje);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendToServidor(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            taServidor.setText(taServidor.getText() + mensaje + "\n");
            System.out.println("Texto actual del JTextPane taServidor: " + taServidor.getText());

        });
        System.out.println("Texto actual del JTextPane taServidor: " + taServidor.getText());

    }


    private void appendToCliente(String mensaje) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                taCliente.setText(taCliente.getText() + mensaje + "\n");
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cliente UDP");
        frame.setContentPane(new ClienteUDP().JPPrincipal);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}