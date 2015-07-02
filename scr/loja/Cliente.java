package loja;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {
    
    Socket cliente;
    Scanner leituraServidor;
    PrintStream escritaServidor;
    
    //--------------------------------------
    // Construtor
    // 	ip    -> Endereço do servidor
    //  porta -> Porto para conexão
    public Cliente(String ip, int porta){
        try {
            cliente = new Socket(ip, porta);
            leituraServidor = new Scanner(cliente.getInputStream());
            escritaServidor = new PrintStream(cliente.getOutputStream());
        } catch (IOException ex) {
            System.out.println("Erro ao se conectar ao servidor: " + ex);
        }
        processaInformacoesRecebidas();
    }
    
    public static void main(String[] args){
	// Argumentos recebidos pela linha de comando
        new Cliente(args[0], Integer.parseInt(args[1]));
    }

    //-------------------------
    // Menu de Login/Cadastro
    private void processaInformacoesRecebidas() {
        System.out.println("\n1 - Logar");
        System.out.println("2 - Registrar");
        System.out.print  ("Opcao: ");
        String opcao = new Scanner(System.in).nextLine();
        switch (opcao){
            case "1": logar(); break;
            case "2": registrar(); break;
        }
        processaInformacoesRecebidas();
    }

    //---------------------------------------------------
    // Responsável pela entrada e processamento do login
    private void logar() {
        
	// Entradas
        System.out.print("\nUsuario: ");
        String user = new Scanner(System.in).nextLine();
        System.out.print("Senha: ");
        String pass = new Scanner(System.in).nextLine();
	// Processamento
        escritaServidor.println("Login");
        escritaServidor.println(user);
        escritaServidor.println(pass);
        
	// Sincronização com o Servidor
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
	// Verifica os resultados da tentativa de entrar
        if (leituraServidor.nextLine().equals("Sucess!")){
            System.out.println("\nLogado com sucesso!");
            new Thread( () -> { recebimento(); }).start();
            menu();
        }
        else
            System.out.println("\nUsuario ou senha incorreta");
    }
    
    //-----------------------------------------------------------
    // Processa o que é recebido do servidor (Compra e Listagem)
    private void recebimento(){
        while (leituraServidor.hasNextLine()){
            String line = leituraServidor.nextLine();
            switch (line) {
                case "Comprado!":
                    System.out.println("\nCompra realizada com sucesso!");
                    break;
                case "Estoque!":
                    System.out.println("\nNao temos esta quantidade em estoque.");
                    break;
                case "Produto!":
                    System.out.println("\nProduto nao encontrado.");
                    break;
                default:
                    System.out.println(line);
            }
        }
    }
	
    //--------------------------
    // Menu Principal
    private void menu(){
        System.out.println("\nMenu\n");
        System.out.println("1 - Listar");
        System.out.println("2 - Comprar");
        System.out.println("3 - Sair");
        System.out.print("Opcao: ");
        String opcao = new Scanner(System.in).nextLine();
        switch (opcao){
            case "1": listar(); break;
            case "2": comprar(); break;
            case "3": sair(); break;
        }
        menu();
    }

    //----------------------------------------
    // Registra um novo usuário
    private void registrar() {
	// Entradas
        System.out.print("\nUsuario: ");
        String user = new Scanner(System.in).nextLine();
        System.out.print("Senha: ");
        String pass = new Scanner(System.in).nextLine();
	// Processamento no servidor
        escritaServidor.println("Registro");
        escritaServidor.println(user);
        escritaServidor.println(pass);
        
	// Sincronização com o servidor
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
	// Avalia os resultados
        if (leituraServidor.nextLine().equals("Sucess!"))
            System.out.println("Usuario cadastrado com sucesso!");
        else
            System.out.println("Usuario ja existe!");

    }

    //------------------
    // Fecha o programa
    private void sair() {
        System.exit(0);
    }

    //-------------------------
    // Lista todos os produtos
    private void listar() {
	// Processa no servidor
        escritaServidor.println("Listar");
	// Sincronização com o servidor
        try {
            Thread.sleep(700);
        } catch (InterruptedException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //-------------------------
    // Compra único produto
    private void comprar() {
	// Entradas
        System.out.print("\nProduto: ");
        String nome = new Scanner(System.in).nextLine();
        System.out.print("Empresa: ");
        String empresa = new Scanner(System.in).nextLine();
        System.out.print("Quantidade: ");
        String quant = new Scanner(System.in).nextLine();
        
	// Processamento no servidor
        escritaServidor.println("Comprar");
        escritaServidor.println(nome);
        escritaServidor.println(empresa);
        escritaServidor.println(quant);
        
	// Sincronização com o servidor
        try {
            Thread.sleep(700);
        } catch (InterruptedException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}