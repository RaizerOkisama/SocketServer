import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 *
 * DOCUMENTA��O DA CLASSE ---------------------- FINALIDADE: Classe que
 * implementa a parte cliente de um Socket TCP.
 *
 * HIST�RICO DE DESENVOLVIMENTO: Marco 18, 2019 - @author Alexandre A. Amaral
 * - @version 1.0.
 *
 */
public class TCPCliente {

	private ObjectInputStream entrada;
	private ObjectOutputStream saida;
	private Socket s;

	/**
	 * Construtor default da classe.
	 * 
	 * @author Alexandre A. Amaral.
	 */
	public TCPCliente() {
		try {
			String ip = JOptionPane.showInputDialog(null, "Digite o IP do servidor: ");
			while (ValidateIPv4.isValidInet4Address(ip) == false) {
				if(ip.equalsIgnoreCase("SAIR")) {
					return;
				}
				System.out.println("Digite o endereco IP do servidor corretamente.");
				ip = JOptionPane.showInputDialog(null, "Digite o IP do servidor: ");
			}

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
			do {
				try {
					// envia uma mensagem para o servidor
					texto = JOptionPane.showInputDialog(null, "Digite o nome do programa que deseja executar: ");
					saida.writeObject(texto);
					saida.flush();
					// l� os dados recebidos do servidor
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

	/**
	 * M�dodo principal.
	 * 
	 * @author Alexandre A. Amaral.
	 * @param args
	 */    
	public static void main (String args[]) {
		new TCPCliente();
	} 
	
}
