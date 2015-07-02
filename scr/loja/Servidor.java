package loja;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {  
    private ServerSocket Server;
    private static List<Usuario> Usuarios;
    private static List<Produto> Produtos;
    
    //---------------------------------------
    // Construtor
    //  Porta -> Porto de conexão a clientes
    public Servidor(int Porta){
        try{
            this.Server = new ServerSocket(Porta);
        }
        catch(Exception e){
            System.out.println("Erro ao ligar servidor: " + e.getMessage());
        }
    }

    //-----------------------
    // Menu principal
    private static void menu() {
        System.out.println("\nMenu\n");
        System.out.println("1 - Cadastrar produto");
        System.out.println("2 - Listar produtos em estoque");
        System.out.println("3 - Listar produtos esgotados ");
        System.out.println("4 - Aumentar quantidade de um produto");
        System.out.println("5 - Sair");
        System.out.print("Opcao: ");
        switch (new Scanner(System.in).nextInt()){
            case 1: cadastrarProduto(); break;
            case 2: listarProdutosEmEstoque(); break;
            case 3: listarProdutosEsgotados(); break;
            case 4: aumentarQuantidadeDeUmProduto(); break;
            case 5: sair();
        }
        menu();
    }

    //----------------------
    // Inserção dum produto
    private static void cadastrarProduto() {
	// Entradas
        System.out.print("\nNome do Produto: ");
        String nome    = new Scanner(System.in).nextLine();
        System.out.print("Empresa do Produto: ");
        String empresa = new Scanner(System.in).nextLine();
        System.out.print("Preço: ");
        double preco   = new Scanner(System.in).nextDouble();
        System.out.print("Quantidade inicial: ");
        int   quant    = new Scanner(System.in).nextInt();
	// Adiciona na lista de produtos
        boolean add = Produtos.add(new Produto(nome, empresa, preco, quant));
        if (add)
            System.out.println("\nProduto cadastrado com sucesso!");
        
    }

    //-------------------------------------------
    // Lista produtos com quantidade maior que 0
    private static void listarProdutosEmEstoque() {
        Produtos.stream().filter((p) -> (p.Quantidade > 0)).forEach((p) -> {
            System.out.println("Produto: " + p.getNome() + " | Empresa: " +
                    p.getEmpresa() + " | Preço: " + p.getPreco() + " | " +
                    "Quantidade: " + p.getQuantidade());
        });
    }

    //--------------------------------------------
    // Lista produtos com quantidade menor que 1
    private static void listarProdutosEsgotados() {
        Produtos.stream().filter((p) -> (p.Quantidade < 1)).forEach((p) -> {
            System.out.println("Produto: " + p.getNome() + " | Empresa: " +
                    p.getEmpresa() + " | Preço: " + p.getPreco());
        });
    }

    //-----------------------------------------------
    // Aumenta a quantidade em estoque de um produto
    private static void aumentarQuantidadeDeUmProduto() {
        Produto produto = null;
        while (true){
	    // Entrada
            System.out.print("\nNome do produto: ");
            String nome = new Scanner(System.in).nextLine();
            System.out.print("Empresa do produto: ");
            String empresa = new Scanner(System.in).nextLine();
	    // Procura o produto
            for (Produto p : Produtos)
                if (p.getNome().equals(nome) && p.getEmpresa().equals(empresa))
                    produto = p;
                    
            if (produto != null){
                System.out.println("\n\nProduto selecionado!");
                break;
            }
            System.out.println("\n\nProduto não encontrado");
        }
        System.out.print("\nQuantidade a aumentar: ");
        int quant = new Scanner(System.in).nextInt();
	// Aumenta a quantidade se encontrou o produto
        produto.setQuantidade(produto.getQuantidade() + quant);
        System.out.println("\n\nQuantidade atual: " + produto.getQuantidade());
    }
    
    //--------------------------------------------
    // Atualiza os arquivos CSV e fecha o servidor
    private static void sair() {
        escreverCSV("Usuarios.csv", "Produtos.csv");
        System.exit(0);
    }
    
    //----------------------------------------
    // Atualiza o CSV com a lista em RAM
    // utiliza o delimitador | entre os campos
    private static void escreverCSV(String usersString, String productsString) {
        try (PrintWriter csv = new PrintWriter(usersString)) {
            Usuarios.stream().forEach((u) -> {
                csv.println(u.getNick()  + "|" +
                            u.getSenha() + "|"
                            );
            });
        }catch(Exception e){
            System.out.println("Problemas na escrita: " + e);}

        try (PrintWriter csv = new PrintWriter(productsString)) {
            Produtos.stream().forEach((u) -> {
                csv.println(u.getNome()       + "|" +
                            u.getEmpresa()    + "|" +
                            u.getPreco()      + "|" +
                            u.getQuantidade() + "|"
                            );
            });
        }catch(Exception e){
            System.out.println("Problemas na escrita: " + e);}
    }
    
    //--------------------------------------------
    // Carrega os arquivos CSV para listas em RAM
    private static void carregarCSV(String usersString, String productsString){
        Produtos = new ArrayList<>();
        Usuarios = new ArrayList<>();
        BufferedReader in;
        String line; 
        // Usuarios
        try {            
	    // Cria o arquivo se ele ainda não existir
            File arq = new File(usersString);
            if (!arq.exists())
                arq.createNewFile();
            in = new BufferedReader(new FileReader(usersString));
            while ( (line = in.readLine()) != null) {
            	String[] values = line.split("\\|");
                Usuarios.add(new Usuario(values[0], values[1]));
            }
        } catch (Exception e){
            System.out.println("Problema ao processar arquivo: " + e);
        }
        
        // Produtos
        try {            
	    // Cria o arquivo se ele ainda não existir
            File arq = new File(productsString);
            if (!arq.exists())
                arq.createNewFile();
            in = new BufferedReader(new FileReader(productsString));
            while ( (line = in.readLine()) != null) {
            	String[] values = line.split("\\|");
                Produtos.add(new Produto(values[0], values[1],
                        Double.parseDouble(values[2]), Integer.parseInt(values[3])));
            }
        } catch (IOException | NumberFormatException e){
            System.out.println("Problema ao processar arquivo: " + e);
        }
        
    }
    
    //--------------------
    // Processo principal
    public static void main(String[] args){
	// Cria o servidor
        Servidor servidor = new Servidor(Integer.parseInt(args[0]));
	// Thread que espera conexões de clientes
        new Thread( () -> {
            try {
                while (true){
                    Socket client = servidor.Server.accept();
		    // Thread que espera informações dos clientes
                    new Thread( () -> {
                        try {
                            new ManipulaCliente( client.getInputStream(),
                                    new PrintStream(client.getOutputStream()));
                        } catch (IOException ex) {
                            System.out.println(ex);
                        }
                    }).start();
                }
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
        carregarCSV("Usuarios.csv", "Produtos.csv");
        menu();
    }

    private static class Produto {
        private final String Nome;
        private final String Empresa;
        private double Preco;
        private int Quantidade;
        
        public Produto(String Nome, String Empresa, double Preco, int Quantidade){
            this.Nome       = Nome;
            this.Empresa    = Empresa;
            this.Preco      = Preco;
            this.Quantidade = Quantidade;
        }

        /**
         * @return the Nome
         */
        public String getNome() {
            return Nome;
        }

        /**
         * @return the Empresa
         */
        public String getEmpresa() {
            return Empresa;
        }

        /**
         * @return the Preco
         */
        public double getPreco() {
            return Preco;
        }

        /**
         * @return the Quantidade
         */
        public int getQuantidade() {
            return Quantidade;
        }

        /**
         * @param Preco the Preco to set
         */
        public void setPreco(double Preco) {
            this.Preco = Preco;
        }

        /**
         * @param Quantidade the Quantidade to set
         */
        public void setQuantidade(int Quantidade) {
            this.Quantidade = Quantidade;
        }
    }

    private static class Usuario {
        private final String Nick;
        private final String Senha;
        public Usuario(String Nick, String Senha) {
            this.Nick  = Nick;
            this.Senha = Senha;
        }

        /**
         * @return the Nick
         */
        public String getNick() {
            return Nick;
        }

        /**
         * @return the Senha
         */
        public String getSenha() {
            return Senha;
        }
    }
    
    //==================================
    //	ManipulaCliente
    //----------------------------------
    // Responsável por tratar as infos
    // recebidas de cada clientes
    //----------------------------------
    private static class ManipulaCliente{
        private final Scanner     leituraCliente;
        private final PrintStream escritaCliente;
        
        public ManipulaCliente(InputStream input, PrintStream output){
            this.leituraCliente = new Scanner(input);
            this.escritaCliente = output;
            atualizacao();
        }
        
	//---------------------------------------------------------------
	// Para cada opção possível do programa chama o método específico
        public void atualizacao() {
            while (leituraCliente.hasNextLine()){
                String line = leituraCliente.nextLine();
                switch (line){
                    case "Registro": RegistrarUsuario(leituraCliente.nextLine(),
                                                      leituraCliente.nextLine());
                                     break;
                    case "Login"   : LogarUsuario(leituraCliente.nextLine(),
                                                  leituraCliente.nextLine());
                                     break;
                    case "Listar"  : ListarProUsuario();
                                     break;
                    case "Comprar" : ComprarProduto(leituraCliente.nextLine(),
                                                    leituraCliente.nextLine(),
                                                    leituraCliente.nextLine());
                                     break;
                    default: System.out.println(line);
                }
            }
        }

	//-------------------------------
	// Registro dum novo usuário
        private void RegistrarUsuario(String nick, String senha) {
	    // Verifica se já não existe
            for (Usuario u : Usuarios)
                if (u.getNick().equals(nick)){
                    escritaCliente.println("AlreadyExists!");
                    return;
                }
            // Adiciona na lista de Usuários
            Usuarios.add(new Usuario(nick, senha));
            escritaCliente.println("Sucess!");
        }

	//-----------------------------------
	// Entra com um usuário já existente
        private void LogarUsuario(String nick, String senha) {
	    // Procura o usuário com a determinada senha
            for (Usuario u : Usuarios)
                if (u.getNick().equals(nick) && u.getSenha().equals(senha)){
                    escritaCliente.println("Sucess!");
                    return;
                }
            
            escritaCliente.println("Incorrect!");
        }

	//----------------------------------------
	// Lista todos os produtos para o usuário
        private void ListarProUsuario() {
            Produtos.stream().forEach((p) -> {
                escritaCliente.println(
                        "Produto: "       + p.getNome()    +
                        " | Empresa: "    + p.getEmpresa() +
                        " | Preço: "      + p.getPreco()   +
                        " | Quantidade: " + p.getQuantidade());
            });
        }

	//-------------------------------
	// Efetua a compra de um produto
        private void ComprarProduto(String produto, String empresa, String q) {
            int quant = Integer.parseInt(q);
            for (Produto p : Produtos)
                if (p.getNome().equals(produto) && p.getEmpresa().equals(empresa)){
                    if (p.getQuantidade() < quant)
                        escritaCliente.println("Estoque!");
                    else{
                        escritaCliente.println("Comprado!");
                        p.setQuantidade(p.getQuantidade() - quant);
                    }
                    return;
                }
            escritaCliente.println("Produto!");
        }
    }
}
