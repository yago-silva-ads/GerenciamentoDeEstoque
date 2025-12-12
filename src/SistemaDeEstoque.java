import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;


public class SistemaDeEstoque {
	
	public static int lerQuantidadeBlindada(Scanner scanner) {
	    while (true) { 
	        System.out.print("Digite a quantidade: ");
	        String input = scanner.nextLine(); 

	        
	        if (input == null || input.trim().isEmpty()) {
	            System.out.println("❌ Erro: O campo não pode ficar vazio (nulo). Digite um valor.");
	            continue; 
	        }

	        try {
	           
	            int valor = Integer.parseInt(input);

	            
	            if (valor < 0) {
	                System.out.println("❌ Erro: Estoque não pode ser negativo. O mínimo é 0.");
	            } else {
	               
	                return valor;
	            }

	        } catch (NumberFormatException e) {
	            
	            System.out.println("❌ Erro: '" + input + "' não é um número válido. Tente novamente.");
	        }
	    }
	}
	public static double lerPrecoBlindado(Scanner scanner) {
	    while (true) { 
	        System.out.print("Preço (ex: 29.99): ");
	        String input = scanner.nextLine(); 

	        // TRUQUE DE MESTRE: Troca vírgula por ponto para o Java não reclamar
	        input = input.replace(",", ".");

	        if (input == null || input.trim().isEmpty()) {
	            System.out.println("❌ Erro: O preço não pode ser vazio.");
	            continue; 
	        }

	        try {
	            double valor = Double.parseDouble(input);

	            if (valor < 0) {
	                System.out.println("❌ Erro: O preço não pode ser negativo.");
	            } else {
	                return valor;
	            }

	        } catch (NumberFormatException e) {
	            System.out.println("❌ Erro: '" + input + "' não é um preço válido. Use apenas números.");
	        }
	    }
	}

    
    private static String URL;
    private static String USUARIO;
    private static String SENHA; 

    
    private static Scanner scanner = new Scanner(System.in);

    
    private static boolean carregarConfiguracao() {
        Properties props = new Properties();
        
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            props.load(fis);
            URL = props.getProperty("DB_URL"); 
            USUARIO = props.getProperty("DB_USUARIO");
            SENHA = props.getProperty("DB_SENHA");
            
            
            if (URL == null || USUARIO == null || SENHA == null) {
                System.err.println("ERRO: Arquivo 'db.properties' está incompleto.");
                return false;
            }
            return true;
            
        } catch (IOException e) {
            System.err.println("ERRO CRÍTICO: Arquivo 'db.properties' não encontrado!");
            System.err.println("Crie o arquivo na raiz do projeto.");
            return false;
        }
    }


  
    public static void main(String[] args) {
    	
        
       
        if (!carregarConfiguracao()) {
            return; 
        }
        
       
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("ERRO CRÍTICO: Driver JDBC (mysql-connector-j-X.X.XX.jar) não encontrado!");
            System.out.println("Verifique o Build Path do Eclipse.");
            return;
        }
        
        
        int opcao = 0;
        do {
            exibirMenu();
            try {
                String entrada = scanner.nextLine();
                if (!entrada.isEmpty()) {
                    opcao = Integer.parseInt(entrada);
                } else {
                    opcao = 0; 
                }
            } catch (NumberFormatException e) {
                System.err.println("Opção inválida! Digite apenas números.");
                opcao = 0;
            }

            
            switch (opcao) {
                case 1: cadastrarNovoProduto(); break; // CREATE (C)
                case 2: adicionarEstoque(); break;     // UPDATE (U)
                case 3: removerEstoque(); break;       // UPDATE (U)
                case 4: consultarProduto(); break;     // READ (R)
                case 5: listarTodosOsProdutos(); break; // READ (R)
                case 6: alterarProduto(); break;       // UPDATE (U)
                case 7: excluirProduto(); break;       // DELETE (D)
                case 8: System.out.println("Saindo do sistema..."); break;
                default: System.err.println("Opção inválida. Tente novamente.");
            }
            
            
            if (opcao != 8 && opcao != 0) {
                System.out.println("\nPressione [ENTER] para continuar...");
                scanner.nextLine();
            }

        } while (opcao != 8); 
        
        scanner.close(); 
        
    }
    
    
   
    private static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    
    private static void exibirMenu() {
        System.out.println("\n=== SISTEMA DE ESTOQUE (CRUD Completo) ===");
        System.out.println("1. Cadastrar Produto");
        System.out.println("2. Entrada de Estoque");
        System.out.println("3. Saída de Estoque");
        System.out.println("4. Consultar por Código");
        System.out.println("5. Listar Todos os Produtos");
        System.out.println("6. Alterar Nome/Preço"); 
        System.out.println("7. Excluir Produto");   
        System.out.println("8. Sair");                     
        System.out.print("\nEscolha uma opção: ");
    }

    
    
    private static void cadastrarNovoProduto() {
        System.out.println("\n--- 1. Novo Produto ---");
        
        System.out.print("Código (ex: CAM-001): ");
        String codigo = scanner.nextLine();
        
        if (codigo.isEmpty()) {
             System.err.println("Erro: O código não pode ser vazio.");
             return;
        }
        
        if (produtoExiste(codigo)) {
            System.err.println("Erro: Já existe um produto com esse código.");
            return;
        }

        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        
        System.out.print("Qtd Inicial: ");
    	int quantidade = lerQuantidadeBlindada(scanner);
    	double preco = lerPrecoBlindado(scanner);

        String sql = "INSERT INTO produto (codigo, nome, quantidade, preco) VALUES (?, ?, ?, ?)";

        try (Connection con = getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, codigo);
            stmt.setString(2, nome);
            stmt.setInt(3, quantidade);
            stmt.setDouble(4, preco);
            
            stmt.executeUpdate();
            System.out.println("✅ Produto cadastrado no banco!");
            
        } catch (SQLException e) {
            System.err.println("❌ Erro no banco: " + e.getMessage());
        }
    }

   
    
    private static void adicionarEstoque() {
        System.out.println("\n--- 2. Entrada de Estoque ---");
        System.out.print("Digite o código do produto: ");
        String codigo = scanner.nextLine();

        if (!produtoExiste(codigo)) {
            System.err.println("Erro: Produto não encontrado.");
            return;
        }

        System.out.print("Quanto deseja adicionar? ");
        int qtd = lerQuantidadeBlindada(scanner);
        scanner.nextLine(); 

        if (qtd <= 0) {
            System.err.println("Erro: A quantidade deve ser positiva.");
            return;
        }

        String sql = "UPDATE produto SET quantidade = quantidade + ? WHERE codigo = ?";

        try (Connection con = getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, qtd);
            stmt.setString(2, codigo);
            stmt.executeUpdate();
            System.out.println("✅ Estoque atualizado!");
            
        } catch (SQLException e) {
            System.err.println("❌ Erro: " + e.getMessage());
        }
    }

   
  
    private static void removerEstoque() {
        System.out.println("\n--- 3. Saída de Estoque ---");
        System.out.print("Digite o código do produto: ");
        String codigo = scanner.nextLine();

        if (!produtoExiste(codigo)) {
            System.err.println("Erro: Produto não encontrado.");
            return;
        }

        System.out.print("Quanto deseja remover? ");
        int qtd = lerQuantidadeBlindada(scanner);
        scanner.nextLine(); 

        if (qtd <= 0) {
            System.err.println("Erro: A quantidade deve ser positiva.");
            return;
        }

        String sqlCheck = "SELECT quantidade FROM produto WHERE codigo = ?";
        String sqlUpdate = "UPDATE produto SET quantidade = quantidade - ? WHERE codigo = ?";
        
        try (Connection con = getConexao()) {
            
            int estoqueAtual = 0;
            try (PreparedStatement stmtCheck = con.prepareStatement(sqlCheck)) {
                stmtCheck.setString(1, codigo);
                ResultSet rs = stmtCheck.executeQuery();
                if (rs.next()) {
                    estoqueAtual = rs.getInt("quantidade");
                }
            }
            
            if (estoqueAtual >= qtd) {
                try (PreparedStatement stmtUpdate = con.prepareStatement(sqlUpdate)) {
                    stmtUpdate.setInt(1, qtd);
                    stmtUpdate.setString(2, codigo);
                    stmtUpdate.executeUpdate();
                    System.out.println("✅ Saída registrada! Estoque restante: " + (estoqueAtual - qtd));
                }
            } else {
                System.err.println("⚠️ Erro: Estoque insuficiente. (Disponível: " + estoqueAtual + ")");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro: " + e.getMessage());
        }
    }

   
    
    private static void consultarProduto() {
        System.out.println("\n--- 4. Consultar Produto ---");
        System.out.print("Digite o código: ");
        String codigoBusca = scanner.nextLine();

        String sql = "SELECT * FROM produto WHERE codigo = ?";

        try (Connection con = getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, codigoBusca);
            ResultSet rs = stmt.executeQuery(); 

            if (rs.next()) { 
                System.out.println("---------------------------------");
                System.out.println("Código: " + rs.getString("codigo"));
                System.out.println("Nome:   " + rs.getString("nome"));
                System.out.println("Qtd:    " + rs.getInt("quantidade"));
                System.out.printf("Preço:  R$ %.2f%n", rs.getDouble("preco")); 
                System.out.println("---------------------------------");
            } else {
                System.err.println("Erro: Produto não encontrado.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro: " + e.getMessage());
        }
    }

   
    
    private static void listarTodosOsProdutos() {
        System.out.println("\n--- 5. Lista Geral de Produtos ---");
        String sql = "SELECT * FROM produto ORDER BY nome"; 
        
        try (Connection con = getConexao();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) { 

            System.out.printf("%-10s | %-20s | %-5s | %-10s%n", "CÓDIGO", "NOME", "QTD", "PREÇO");
            System.out.println("-------------------------------------------------------");
            
            boolean encontrou = false;
            while (rs.next()) { 
                encontrou = true;
                System.out.printf("%-10s | %-20s | %-5d | R$ %-10.2f%n", 
                    rs.getString("codigo"), 
                    rs.getString("nome"), 
                    rs.getInt("quantidade"), 
                    rs.getDouble("preco"));
            }
            
            if (!encontrou) {
                System.out.println("Nenhum produto cadastrado no banco de dados.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro: " + e.getMessage());
        }
    }
    
    
    private static void alterarProduto() {
        System.out.println("\n--- 6. Alterar Produto ---");
        System.out.print("Digite o código do produto que deseja alterar: ");
        String codigo = scanner.nextLine();

        if (!produtoExiste(codigo)) {
            System.err.println("Erro: Produto não encontrado.");
            return;
        }
        
        System.out.print("Digite o NOVO nome (ou [ENTER] para manter o atual): ");
        String novoNome = scanner.nextLine();
        
        System.out.print("Digite o NOVO preço (ex: 35,90 ou 0 para manter): ");
        double novoPreco = -1;
        while (novoPreco < 0) {
            System.out.print("Novo Preço: ");
            String input = scanner.nextLine().replace(",", ".");
            try {
                novoPreco = Double.parseDouble(input);
                if (novoPreco < 0) System.out.println("❌ Preço não pode ser negativo.");
            } catch (Exception e) {
                System.out.println("❌ Digite um número válido.");
            }
		}
       
        String sql = "UPDATE produto SET ";
        boolean virgula = false;

        if (!novoNome.isEmpty()) {
            sql += "nome = ?";
            virgula = true;
        }
        if (novoPreco > 0) {
            if (virgula) sql += ", ";
            sql += "preco = ?";
        }
        
        sql += " WHERE codigo = ?";
        
        if (novoNome.isEmpty() && novoPreco <= 0) {
            System.out.println("Nenhuma alteração solicitada.");
            return;
        }
        
        try (Connection con = getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            int parametro = 1; 
            if (!novoNome.isEmpty()) {
                stmt.setString(parametro++, novoNome);
            }
            if (novoPreco > 0) {
                stmt.setDouble(parametro++, novoPreco);
            }
            stmt.setString(parametro, codigo); 
            
            stmt.executeUpdate();
            System.out.println("✅ Produto ALTERADO com sucesso!");
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao alterar produto: " + e.getMessage());
        }
    }

    
    private static void excluirProduto() {
        System.out.println("\n--- 7. Excluir Produto ---");
        System.out.print("Digite o código do produto que deseja EXCLUIR: ");
        String codigo = scanner.nextLine();

        if (!produtoExiste(codigo)) {
            System.err.println("Erro: Produto não encontrado.");
            return;
        }
        
        System.out.print("ATENÇÃO: Confirma a exclusão do produto '" + codigo + "'? (s/n): ");
        String confirmacao = scanner.nextLine();
        
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Exclusão cancelada pelo usuário.");
            return;
        }

        String sql = "DELETE FROM produto WHERE codigo = ?";

        try (Connection con = getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, codigo);
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                System.out.println("✅ Produto EXCLUÍDO com sucesso!");
            } else {
                System.err.println("⚠️ Erro: Não foi possível excluir o produto.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao excluir produto: " + e.getMessage());
        }
    }
    
   
    private static boolean produtoExiste(String codigo) {
        String sql = "SELECT 1 FROM produto WHERE codigo = ?"; 
        try (Connection con = getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); 
            
        } catch (SQLException e) {
            System.err.println("Erro ao checar produto: " + e.getMessage());
            return false;
        }
    }
   
        
    
}import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;


public class SistemaDeEstoque {
	
	public static int lerQuantidadeBlindada(Scanner scanner) {
	    while (true) { 
	        System.out.print("Digite a quantidade: ");
	        String input = scanner.nextLine(); 

	        
	        if (input == null || input.trim().isEmpty()) {
	            System.out.println("❌ Erro: O campo não pode ficar vazio (nulo). Digite um valor.");
	            continue; 
	        }

	        try {
	           
	            int valor = Integer.parseInt(input);

	            
	            if (valor < 0) {
	                System.out.println("❌ Erro: Estoque não pode ser negativo. O mínimo é 0.");
	            } else {
	               
	                return valor;
	            }

	        } catch (NumberFormatException e) {
	            
	            System.out.println("❌ Erro: '" + input + "' não é um número válido. Tente novamente.");
	        }
	    }
	}
	public static double lerPrecoBlindado(Scanner scanner) {
	    while (true) { 
	        System.out.print("Preço (ex: 29.99): ");
	        String input = scanner.nextLine(); 

	        // TRUQUE DE MESTRE: Troca vírgula por ponto para o Java não reclamar
	        input = input.replace(",", ".");

	        if (input == null || input.trim().isEmpty()) {
	            System.out.println("❌ Erro: O preço não pode ser vazio.");
	            continue; 
	        }

	        try {
	            double valor = Double.parseDouble(input);

	            if (valor < 0) {
	                System.out.println("❌ Erro: O preço não pode ser negativo.");
	            } else {
	                return valor;
	            }

	        } catch (NumberFormatException e) {
	            System.out.println("❌ Erro: '" + input + "' não é um preço válido. Use apenas números.");
	        }
	    }
	}

    
    private static String URL;
    private static String USUARIO;
    private static String SENHA; 

    
    private static Scanner scanner = new Scanner(System.in);

    
    private static boolean carregarConfiguracao() {
        Properties props = new Properties();
        
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            props.load(fis);
            URL = props.getProperty("DB_URL"); 
            USUARIO = props.getProperty("DB_USUARIO");
            SENHA = props.getProperty("DB_SENHA");
            
            
            if (URL == null || USUARIO == null || SENHA == null) {
                System.err.println("ERRO: Arquivo 'db.properties' está incompleto.");
                return false;
            }
            return true;
            
        } catch (IOException e) {
            System.err.println("ERRO CRÍTICO: Arquivo 'db.properties' não encontrado!");
            System.err.println("Crie o arquivo na raiz do projeto.");
            return false;
        }
    }


  
    public static void main(String[] args) {
    	
        
       
        if (!carregarConfiguracao()) {
            return; 
        }
        
       
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("ERRO CRÍTICO: Driver JDBC (mysql-connector-j-X.X.XX.jar) não encontrado!");
            System.out.println("Verifique o Build Path do Eclipse.");
            return;
        }
        
        
        int opcao = 0;
        do {
            exibirMenu();
            try {
                String entrada = scanner.nextLine();
                if (!entrada.isEmpty()) {
                    opcao = Integer.parseInt(entrada);
                } else {
                    opcao = 0; 
                }
            } catch (NumberFormatException e) {
                System.err.println("Opção inválida! Digite apenas números.");
                opcao = 0;
            }

            
            switch (opcao) {
                case 1: cadastrarNovoProduto(); break; // CREATE (C)
                case 2: adicionarEstoque(); break;     // UPDATE (U)
                case 3: removerEstoque(); break;       // UPDATE (U)
                case 4: consultarProduto(); break;     // READ (R)
                case 5: listarTodosOsProdutos(); break; // READ (R)
                case 6: alterarProduto(); break;       // UPDATE (U)
                case 7: excluirProduto(); break;       // DELETE (D)
                case 8: System.out.println("Saindo do sistema..."); break;
                default: System.err.println("Opção inválida. Tente novamente.");
            }
            
            
            if (opcao != 8 && opcao != 0) {
                System.out.println("\nPressione [ENTER] para continuar...");
                scanner.nextLine();
            }

        } while (opcao != 8); 
        
        scanner.close(); 
        
    }
    
    
   
    private static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    
    private static void exibirMenu() {
        System.out.println("\n=== SISTEMA DE ESTOQUE (CRUD Completo) ===");
        System.out.println("1. Cadastrar Produto");
        System.out.println("2. Entrada de Estoque");
        System.out.println("3. Saída de Estoque");
        System.out.println("4. Consultar por Código");
        System.out.println("5. Listar Todos os Produtos");
        System.out.println("6. Alterar Nome/Preço"); 
        System.out.println("7. Excluir Produto");   
        System.out.println("8. Sair");                     
        System.out.print("\nEscolha uma opção: ");
    }

    
    
    private static void cadastrarNovoProduto() {
        System.out.println("\n--- 1. Novo Produto ---");
        
        System.out.print("Código (ex: CAM-001): ");
        String codigo = scanner.nextLine();
        
        if (codigo.isEmpty()) {
             System.err.println("Erro: O código não pode ser vazio.");
             return;
        }
        
        if (produtoExiste(codigo)) {
            System.err.println("Erro: Já existe um produto com esse código.");
            return;
        }

        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        
        System.out.print("Qtd Inicial: ");
    	int quantidade = lerQuantidadeBlindada(scanner);
    	double preco = lerPrecoBlindado(scanner);

        String sql = "INSERT INTO produto (codigo, nome, quantidade, preco) VALUES (?, ?, ?, ?)";

        try (Connection con = getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, codigo);
            stmt.setString(2, nome);
            stmt.setInt(3, quantidade);
            stmt.setDouble(4, preco);
            
            stmt.executeUpdate();
            System.out.println("✅ Produto cadastrado no banco!");
            
        } catch (SQLException e) {
            System.err.println("❌ Erro no banco: " + e.getMessage());
        }
    }

   
    
    private static void adicionarEstoque() {
        System.out.println("\n--- 2. Entrada de Estoque ---");
        System.out.print("Digite o código do produto: ");
        String codigo = scanner.nextLine();

        if (!produtoExiste(codigo)) {
            System.err.println("Erro: Produto não encontrado.");
            return;
        }

        System.out.print("Quanto deseja adicionar? ");
        int qtd = lerQuantidadeBlindada(scanner);
        scanner.nextLine(); 

        if (qtd <= 0) {
            System.err.println("Erro: A quantidade deve ser positiva.");
            return;
        }

        String sql = "UPDATE produto SET quantidade = quantidade + ? WHERE codigo = ?";

        try (Connection con = getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, qtd);
            stmt.setString(2, codigo);
            stmt.executeUpdate();
            System.out.println("✅ Estoque atualizado!");
            
        } catch (SQLException e) {
            System.err.println("❌ Erro: " + e.getMessage());
        }
    }

   
  
    private static void removerEstoque() {
        System.out.println("\n--- 3. Saída de Estoque ---");
        System.out.print("Digite o código do produto: ");
        String codigo = scanner.nextLine();

        if (!produtoExiste(codigo)) {
            System.err.println("Erro: Produto não encontrado.");
            return;
        }

        System.out.print("Quanto deseja remover? ");
        int qtd = lerQuantidadeBlindada(scanner);
        scanner.nextLine(); 

        if (qtd <= 0) {
            System.err.println("Erro: A quantidade deve ser positiva.");
            return;
        }

        String sqlCheck = "SELECT quantidade FROM produto WHERE codigo = ?";
        String sqlUpdate = "UPDATE produto SET quantidade = quantidade - ? WHERE codigo = ?";
        
        try (Connection con = getConexao()) {
            
            int estoqueAtual = 0;
            try (PreparedStatement stmtCheck = con.prepareStatement(sqlCheck)) {
                stmtCheck.setString(1, codigo);
                ResultSet rs = stmtCheck.executeQuery();
                if (rs.next()) {
                    estoqueAtual = rs.getInt("quantidade");
                }
            }
            
            if (estoqueAtual >= qtd) {
                try (PreparedStatement stmtUpdate = con.prepareStatement(sqlUpdate)) {
                    stmtUpdate.setInt(1, qtd);
                    stmtUpdate.setString(2, codigo);
                    stmtUpdate.executeUpdate();
                    System.out.println("✅ Saída registrada! Estoque restante: " + (estoqueAtual - qtd));
                }
            } else {
                System.err.println("⚠️ Erro: Estoque insuficiente. (Disponível: " + estoqueAtual + ")");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro: " + e.getMessage());
        }
    }

   
    
    private static void consultarProduto() {
        System.out.println("\n--- 4. Consultar Produto ---");
        System.out.print("Digite o código: ");
        String codigoBusca = scanner.nextLine();

        String sql = "SELECT * FROM produto WHERE codigo = ?";

        try (Connection con = getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, codigoBusca);
            ResultSet rs = stmt.executeQuery(); 

            if (rs.next()) { 
                System.out.println("---------------------------------");
                System.out.println("Código: " + rs.getString("codigo"));
                System.out.println("Nome:   " + rs.getString("nome"));
                System.out.println("Qtd:    " + rs.getInt("quantidade"));
                System.out.printf("Preço:  R$ %.2f%n", rs.getDouble("preco")); 
                System.out.println("---------------------------------");
            } else {
                System.err.println("Erro: Produto não encontrado.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro: " + e.getMessage());
        }
    }

   
    
    private static void listarTodosOsProdutos() {
        System.out.println("\n--- 5. Lista Geral de Produtos ---");
        String sql = "SELECT * FROM produto ORDER BY nome"; 
        
        try (Connection con = getConexao();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) { 

            System.out.printf("%-10s | %-20s | %-5s | %-10s%n", "CÓDIGO", "NOME", "QTD", "PREÇO");
            System.out.println("-------------------------------------------------------");
            
            boolean encontrou = false;
            while (rs.next()) { 
                encontrou = true;
                System.out.printf("%-10s | %-20s | %-5d | R$ %-10.2f%n", 
                    rs.getString("codigo"), 
                    rs.getString("nome"), 
                    rs.getInt("quantidade"), 
                    rs.getDouble("preco"));
            }
            
            if (!encontrou) {
                System.out.println("Nenhum produto cadastrado no banco de dados.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro: " + e.getMessage());
        }
    }
    
    
    private static void alterarProduto() {
        System.out.println("\n--- 6. Alterar Produto ---");
        System.out.print("Digite o código do produto que deseja alterar: ");
        String codigo = scanner.nextLine();

        if (!produtoExiste(codigo)) {
            System.err.println("Erro: Produto não encontrado.");
            return;
        }
        
        System.out.print("Digite o NOVO nome (ou [ENTER] para manter o atual): ");
        String novoNome = scanner.nextLine();
        
        System.out.print("Digite o NOVO preço (ex: 35,90 ou 0 para manter): ");
        double novoPreco = -1;
        while (novoPreco < 0) {
            System.out.print("Novo Preço: ");
            String input = scanner.nextLine().replace(",", ".");
            try {
                novoPreco = Double.parseDouble(input);
                if (novoPreco < 0) System.out.println("❌ Preço não pode ser negativo.");
            } catch (Exception e) {
                System.out.println("❌ Digite um número válido.");
            }
		}
       
        String sql = "UPDATE produto SET ";
        boolean virgula = false;

        if (!novoNome.isEmpty()) {
            sql += "nome = ?";
            virgula = true;
        }
        if (novoPreco > 0) {
            if (virgula) sql += ", ";
            sql += "preco = ?";
        }
        
        sql += " WHERE codigo = ?";
        
        if (novoNome.isEmpty() && novoPreco <= 0) {
            System.out.println("Nenhuma alteração solicitada.");
            return;
        }
        
        try (Connection con = getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            int parametro = 1; 
            if (!novoNome.isEmpty()) {
                stmt.setString(parametro++, novoNome);
            }
            if (novoPreco > 0) {
                stmt.setDouble(parametro++, novoPreco);
            }
            stmt.setString(parametro, codigo); 
            
            stmt.executeUpdate();
            System.out.println("✅ Produto ALTERADO com sucesso!");
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao alterar produto: " + e.getMessage());
        }
    }

    
    private static void excluirProduto() {
        System.out.println("\n--- 7. Excluir Produto ---");
        System.out.print("Digite o código do produto que deseja EXCLUIR: ");
        String codigo = scanner.nextLine();

        if (!produtoExiste(codigo)) {
            System.err.println("Erro: Produto não encontrado.");
            return;
        }
        
        System.out.print("ATENÇÃO: Confirma a exclusão do produto '" + codigo + "'? (s/n): ");
        String confirmacao = scanner.nextLine();
        
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Exclusão cancelada pelo usuário.");
            return;
        }

        String sql = "DELETE FROM produto WHERE codigo = ?";

        try (Connection con = getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, codigo);
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                System.out.println("✅ Produto EXCLUÍDO com sucesso!");
            } else {
                System.err.println("⚠️ Erro: Não foi possível excluir o produto.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao excluir produto: " + e.getMessage());
        }
    }
    
   
    private static boolean produtoExiste(String codigo) {
        String sql = "SELECT 1 FROM produto WHERE codigo = ?"; 
        try (Connection con = getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); 
            
        } catch (SQLException e) {
            System.err.println("Erro ao checar produto: " + e.getMessage());
            return false;
        }
    }
   
        
    
}
