import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;

public class TCPCliente {
	private ObjectInputStream entrada;
	private ObjectOutputStream saida;
	private Socket s;

	public TCPCliente() {
		try {
			String ip = JOptionPane.showInputDialog(null, "Digite o IP do servidor: ");
			// Enquanto o usuário não informar um endereço válido ou digitar "Sair" fica em loop
			while (ValidateIPv4.isValidInet4Address(ip) == false) {
				if(ip.equalsIgnoreCase("SAIR")) {
					return;
				}
				System.out.println("Digite o endereco IP do servidor corretamente.");
				ip = JOptionPane.showInputDialog(null, "Digite o IP do servidor: ");
			}
			// Tenta conectar com o servidor
			try {
				s = new Socket(ip, 5000);
				System.out.println(">>| A conexao com o servidor foi realizada com sucesso |<<");
				System.out.println(">>| Info. servidor - " + s.getRemoteSocketAddress() + " |<<");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("ERRO: Nao foi possivel estabelecer uma conexao com o servidor.");
			}
			saida = new ObjectOutputStream(s.getOutputStream());
			entrada = new ObjectInputStream(s.getInputStream());
			String texto = "";
			// Enquanto o usuário não digitar sair ou fechar a janela de diálogo fica em loop
			do {
				try {
					// Envia o nome ou a path do programa que deve ser executado para o servidor;
					texto = JOptionPane.showInputDialog(null, "Digite o nome do programa que deseja executar: ");
					saida.writeObject(texto);
					saida.flush();
					// Lê os dados recebidos do servidor
					System.out.println(entrada.readObject());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while(!texto.equalsIgnoreCase("SAIR"));	
			// finaliza a conex�o e fecha os streams e sockets
			entrada.close();
			saida.close();
			s.close();			
		} catch(Exception e) {
			e.printStackTrace();
		} 
		
	}
	// Método principal
	public static void main (String args[]) {
		new TCPCliente();
	} 
	
}
