import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
/**
 *
 * DOCUMENTA��O DA CLASSE 
 * ---------------------- 
 * FINALIDADE: Classe que implementa a parte servidor de um Socket
 * TCP. Aceita diversos clientes simultanemante.
 *
 * HIST�RICO DE DESENVOLVIMENTO: 
 * Marco 18, 2019 - @author Alexandre A. Amaral -  @version 1.0.
 *
 */
public class TCPServerMultiCliente {

	private ServerSocket ss;
	private ObjectInputStream entrada; 
	private ObjectOutputStream saida; 

	/**
	 * Construtor default da classe.
	 * 
	 * @author Alexandre A. Amaral.
	 */    
	public TCPServerMultiCliente() { 

		try { 
			this.ss = new ServerSocket(5000); 

			while(true){
				// aceita as conex�es 
				Socket clientSocket = ss.accept();
	
				// inicia a thread para o cliente  
				new Thread(new ClienteThread(clientSocket)).start();
			}			 

		} 
		catch(Exception e) { 
			e.printStackTrace();
		}finally{
			try {
				this.ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	} 


	/**
	 *
	 * DOCUMENTA��O DA CLASSE
	 * ----------------------
	 * FINALIDADE:
	 * Inner class que tem a finalidade de tratar a conex�o com cada cliente.
	 *
	 * HIST�RICO DE DESENVOLVIMENTO:
	 * Mar�o 18, 2019 - @author Alexandre A. Amaral - Primeira vers�o da classe.
	 *
	 */	
	class ClienteThread implements Runnable {  

		private Socket novoCliente;
		
		/**
		 * Construtor default da classe.
		 * 
		 * @author Alexandre A. Amaral.
		 */   		
		public ClienteThread(Socket s) { 
			this.novoCliente = s; 
		} 

		@Override
		public void run() {            

			// obt�m o identificador da Thread
			long idThread = Thread.currentThread().getId();            

			// mostra informa��es detalhadas da conex�o  
			System.out.println("Cliente conectado - " + this.novoCliente.getRemoteSocketAddress()); 

			try {

				entrada = new ObjectInputStream(this.novoCliente.getInputStream()); 
				saida = new ObjectOutputStream(this.novoCliente.getOutputStream()); 

				Object valor;
				// fica em loop at� a Thread do cliente for falsa (fechar a conex�o) 
				do {                  
					// l� entrada
					valor = entrada.readObject();
					if (valor.toString().isEmpty()){
						saida.writeObject("Servidor(" + idThread + "): Digite o nome do programa que você deseja executar.");
						saida.flush();
					} else {
						try {
							// Abre o processo
							ProcessBuilder builder = new ProcessBuilder((String) valor);
							Process pro = builder.start();
							// envia a resposta para o cliente
							saida.writeObject("O programa " + valor.toString().toUpperCase() + " foi executado com sucesso !");
						} catch (Exception e){
							if(valor.toString().equalsIgnoreCase("SAIR")) {
								saida.writeObject("See You Later");
							} else {
							saida.writeObject("Nao foi possivel executar o programa "+ valor.toString().toUpperCase() +", tente novamente.");
							e.printStackTrace();
							}
						} finally {
							saida.flush(); 	
						}
					}
				} while(!valor.toString().equalsIgnoreCase("SAIR")); 
			} 
			catch(Exception e) { 
				e.printStackTrace(); 
			} 
			finally { 
				try {                    
					// fecha a conex�o
					entrada.close(); 
					saida.close(); 
					novoCliente.close(); 	                    
				} 
				catch(IOException ioe) { 
					ioe.printStackTrace(); 
				} 
			} 
		} 

	} // fim inner class
	/**
	 * M�dodo principal.
	 * 
	 * @author Alexandre A. Amaral.
	 * @param args
	 */    
	public static void main (String args[]) {
		new TCPServerMultiCliente();
	} 
	
}
