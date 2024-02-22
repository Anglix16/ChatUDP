package Util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class ServidorUDP {
    private static final int PUERTO = 12345; // Puerto del servidor
    private static final List<InfoCliente> clientes = new ArrayList<>(); // Lista de clientes conectados

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(PUERTO); // Crear socket UDP en el puerto especificado
            System.out.println("Servidor iniciado..."); // Indicar que el servidor se ha iniciado correctamente

            while (true) {
                byte[] buffer = new byte[1024]; // Crear un buffer para almacenar los datos recibidos
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length); // Crear un paquete para recibir datos
                socket.receive(paquete); // Recibir datos del cliente

                String mensaje = new String(paquete.getData(), 0, paquete.getLength()); // Convertir los datos recibidos en una cadena de texto
                System.out.println("Mensaje recibido desde el cliente: " + mensaje); // Mostrar el mensaje recibido desde el cliente

                // Transmitir el mensaje a todos los clientes excepto al que lo envió originalmente
                for (InfoCliente cliente : clientes) {
                    if (!cliente.getDireccion().equals(paquete.getAddress()) || cliente.getPuerto() != paquete.getPort()) {
                        DatagramPacket paqueteRespuesta = new DatagramPacket(buffer, buffer.length,
                                cliente.getDireccion(), cliente.getPuerto());
                        socket.send(paqueteRespuesta); // Enviar el mensaje al cliente
                        System.out.println("Enviando mensaje al cliente: " + mensaje); // Indicar que se ha enviado el mensaje
                    }
                }

                // Agregar nuevo cliente si no existe en la lista
                if (!clienteExiste(paquete.getAddress(), paquete.getPort())) {
                    clientes.add(new InfoCliente(paquete.getAddress(), paquete.getPort())); // Agregar nuevo cliente a la lista
                    System.out.println("Nuevo cliente conectado: " + paquete.getAddress() + ":" + paquete.getPort()); // Mostrar que un nuevo cliente se ha conectado
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Manejar cualquier excepción de E/S
        }
    }

    // Comprobar si un cliente ya existe en la lista de clientes conectados
    private static boolean clienteExiste(java.net.InetAddress direccion, int puerto) {
        for (InfoCliente cliente : clientes) {
            if (cliente.getDireccion().equals(direccion) && cliente.getPuerto() == puerto) {
                return true; // Devolver verdadero si el cliente ya existe en la lista
            }
        }
        return false; // Devolver falso si el cliente no existe en la lista
    }
}

// Clase para representar la información de un cliente (dirección IP y puerto)
class InfoCliente {
    private final java.net.InetAddress direccion; // Dirección IP del cliente
    private final int puerto; // Puerto del cliente

    // Constructor de la clase
    public InfoCliente(java.net.InetAddress direccion, int puerto) {
        this.direccion = direccion;
        this.puerto = puerto;
    }

    // Método para obtener la dirección IP del cliente
    public java.net.InetAddress getDireccion() {
        return direccion;
    }

    // Método para obtener el puerto del cliente
    public int getPuerto() {
        return puerto;
    }
}
