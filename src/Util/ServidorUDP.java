package Util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class ServidorUDP {
    private static final int PUERTO = 12345;
    private static final List<InfoCliente> clientes = new ArrayList<>();

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(PUERTO);
            System.out.println("Servidor iniciado...");

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                String mensaje = new String(paquete.getData(), 0, paquete.getLength());
                System.out.println("Recibido desde el cliente: " + mensaje);

                // Transmitir el mensaje a todos los clientes
                for (InfoCliente cliente : clientes) {
                    if (!cliente.getDireccion().equals(paquete.getAddress())) {
                        DatagramPacket paqueteRespuesta = new DatagramPacket(buffer, buffer.length,
                                cliente.getDireccion(), cliente.getPuerto());
                        socket.send(paqueteRespuesta);
                    }
                }

                // Agregar nuevo cliente
                if (!clienteExiste(paquete.getAddress(), paquete.getPort())) {
                    clientes.add(new InfoCliente(paquete.getAddress(), paquete.getPort()));
                    System.out.println("Nuevo cliente conectado: " + paquete.getAddress() + ":" + paquete.getPort());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean clienteExiste(java.net.InetAddress direccion, int puerto) {
        for (InfoCliente cliente : clientes) {
            if (cliente.getDireccion().equals(direccion) && cliente.getPuerto() == puerto) {
                return true;
            }
        }
        return false;
    }
}

class InfoCliente {
    private final java.net.InetAddress direccion;
    private final int puerto;

    public InfoCliente(java.net.InetAddress direccion, int puerto) {
        this.direccion = direccion;
        this.puerto = puerto;
    }

    public java.net.InetAddress getDireccion() {
        return direccion;
    }

    public int getPuerto() {
        return puerto;
    }
}
