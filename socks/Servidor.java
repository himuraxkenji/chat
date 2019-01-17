package socks;



import javax.swing.*;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor  {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MarcoServidor mimarco=new MarcoServidor();
		
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
	}	
}

class MarcoServidor extends JFrame implements Runnable{
	
	public MarcoServidor(){
		
		setBounds(1200,300,280,350);				
			
		JPanel milamina= new JPanel();
		
		milamina.setLayout(new BorderLayout());
		
		areatexto=new JTextArea();
		
		milamina.add(areatexto,BorderLayout.CENTER);
		
		add(milamina);
		
		setVisible(true);
		
		Thread miHilo = new Thread(this);
		
		miHilo.start();
		}
	
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			
			ServerSocket servidor = new ServerSocket(60120);
			
			String nick, ip, mensaje; 
			
			PaqueteEnvio  paqueteRecibido; 
			
			ArrayList <String> listaIp = new ArrayList<String>();
			
			while (true) {	
				
				Socket miSocket = servidor.accept();
				
//				DataInputStream flujoEntrada = new DataInputStream(miSocket.getInputStream());
//				
//				String mensajeTexto = flujoEntrada.readUTF();
				
//				areatexto.append("\n" + mensajeTexto);
				ObjectInputStream paqueteDatos = new ObjectInputStream(miSocket.getInputStream());
				
				
				paqueteRecibido = (PaqueteEnvio) paqueteDatos.readObject();
				
				nick = paqueteRecibido.getNick();
				
				ip  = paqueteRecibido.getIp();
				
				mensaje = paqueteRecibido.getMensaje();
			
				if (!mensaje.equals(" online")) {
				
					areatexto.append("\n" + nick + " : " + mensaje + " para " + ip);
					
					Socket enviaDestinatario = new Socket(ip, 60121);
					
					ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
					
					paqueteReenvio.writeObject(paqueteRecibido);
					
					paqueteReenvio.close();
					
					enviaDestinatario.close();
					
					miSocket.close();
					
				}else {
					
					// ------------------- DETECTA ONLINE -----------------------
					
					InetAddress localizacion = miSocket.getInetAddress();
					
					String ipCliente = localizacion.getHostAddress();
					
					System.out.println("Online: " + ipCliente);
					
					listaIp.add(ipCliente);
						
					paqueteRecibido.setDireccionesIp(listaIp);
					
					for (String direccionIP: listaIp) {
						
						Socket enviaDestinatario = new Socket(direccionIP, 60121);
						
						ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
						
						paqueteReenvio.writeObject(paqueteRecibido);
						
						paqueteReenvio.close();
						
						enviaDestinatario.close();
						
						miSocket.close();
						
					}
					
					// ----------------------------------------------------------			
				}
				
				
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private	JTextArea areatexto;
}
