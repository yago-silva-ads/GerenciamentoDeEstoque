📦 Sistema de Gerenciamento de Estoque (CRUD)

Projeto de faculdade para a disciplina de Algoritmos e Programação, focado em criar um CRUD (Create, Read, Update, Delete) completo e funcional em Java, com persistência de dados em um banco MySQL.

O projeto é executado inteiramente no console (terminal) e utiliza a arquitetura procedural (métodos estáticos) para simular um sistema de software de gestão (ERP) básico.

Confira o protótipo de alta fidelidade deste sistema no Figma: [Link]📦 Sistema de Gerenciamento de Estoque (CRUD)

Projeto de faculdade para a disciplina de Algoritmos e Programação, focado em criar um CRUD (Create, Read, Update, Delete) completo e funcional em Java, com persistência de dados em um banco MySQL.

O projeto é executado inteiramente no console (terminal) e utiliza a arquitetura procedural (métodos estáticos) para simular um sistema de software de gestão (ERP) básico.

🎨 **Confira o protótipo de alta fidelidade no Figma:** [Acesse aqui o Trabalho UX](https://www.figma.com/file/TMlaH6i6uCvMQurqfkiESR/TrabalhoUX)

✨ Funcionalidades (CRUD Completo)

(C) Create: Cadastrar novos produtos.

(R) Read: Listar todos os produtos (ordenados por nome) ou consultar um produto específico por código.

(U) Update: Atualizar estoque (entrada/saída) e alterar dados (nome/preço) de um produto.

(D) Delete: Excluir um produto do banco de dados (com confirmação de segurança).

🛠️ Tecnologias Utilizadas

🛡️ Diferenciais de Qualidade (Data Blindagem)

Como parte dos meus estudos em Qualidade de Software, implementei uma camada de validação de entrada (blindagem) para garantir a integridade dos dados e a resiliência do sistema:

• Validação de Tipos: Uso de blocos try-catch para capturar NumberFormatException, impedindo que a aplicação quebre ao receber caracteres em campos numéricos.

• Prevenção de Dados Inválidos: Métodos como lerQuantidadeBlindada e lerPrecoBlindado que utilizam loops while para rejeitar entradas nulas, vazias ou valores negativos.

• Segurança de Credenciais: Separação de dados sensíveis (usuário e senha do banco) em um arquivo db.properties externo, seguindo boas práticas de segurança.

Linguagem: Java (JDK 21)

Banco de Dados: MySQL 8.0

Driver de Conexão: MySQL Connector/J (JDBC)

Segurança: As senhas e credenciais do banco são gerenciadas externamente através de um arquivo .properties (não incluso no repositório por segurança).

🚀 Como Rodar o Projeto
Para executar este projeto na sua máquina local, siga os passos:
  # Para clonar o repositório, use o comando abaixo:
git clone https://github.com/yago-silva-ads/GerenciamentoDeEstoque.git

Banco de Dados:

Execute o script SQL abaixo no seu MySQL Workbench para criar a tabela:



CREATE SCHEMA IF NOT EXISTS db\_estoque;

USE db\_estoque;

CREATE TABLE IF NOT EXISTS produto (

    id INT PRIMARY KEY AUTO\_INCREMENT,

    codigo VARCHAR(20) NOT NULL UNIQUE,

    nome VARCHAR(100) NOT NULL,

    quantidade INT,

    preco DECIMAL(10, 2)

);

Credenciais (Importante):

Na raiz do projeto (fora da pasta src), crie um arquivo chamado db.properties.

Adicione suas credenciais do MySQL nele:



DB\_URL=jdbc:mysql://localhost:3306/db\_estoque

DB\_USUARIO=root

DB\_SENHA=sua\_senha\_aqui

Eclipse:

Importe o projeto no Eclipse.

Adicione o mysql-connector-j-X.X.XX.jar ao Build Path do projeto.

Execute o arquivo SistemaDeEstoque.java.






