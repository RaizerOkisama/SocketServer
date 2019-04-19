import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerMultiCliente {

	private ServerSocket ss;
	private ObjectInputStream entrada; 
	private ObjectOutputStream saida; 
  
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
		} finally {
			try {
				this.ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	} 

class ClienteThread implements Runnable {  

	private Socket novoCliente;
			
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
				// l� entrada do usuário
				valor = entrada.readObject();
				if (valor.toString().isEmpty()){
					saida.writeObject("Servidor(" + idThread + "): Digite o nome do programa que você deseja executar.");
					saida.flush();
				} else {
					try {
						// Abre o processo
						ProcessBuilder builder = new ProcessBuilder((String) valor);
						Process pro = builder.start();
						// Envia um resposta de sucesso ao cliente.
						saida.writeObject("O programa " + valor.toString().toUpperCase() + " foi executado com sucesso !");
					} catch (Exception e){
						if(valor.toString().equalsIgnoreCase("SAIR")) {
							saida.writeObject("See You Later");
						} else {
						// Envia uma mensagem de falha ao cliente.
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

} 
	 // Metodo principal. 
	public static void main (String args[]) {
		new TCPServerMultiCliente();
	} 
	
}
