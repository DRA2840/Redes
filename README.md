# Proxy

Este projeto foi desenvolvido como trabalho prático da disciplina Redes de Computadores do Departamento de Ciência da Computação da Universidade de Brasília no segundo semestre de 2014, sob supervisão do professor Marcos Fagundes Caetano. 

#### Requisitos (exigidos pelo professor):

* Deve rodar __obrigatoriamente__ na plataforma GNU/Linux;
* Ser executado a partir de linha de comando e aceitar parâmetros de configuração, tais como:
  * IP, porta, arquivo contendo _blacklist_ de URL ou arquivo contendo _whitelist_ de URL
  * É importante destacar que os modos de operação _blacklist_ e _whitelist_ são excludentes:
    * No modo de operação _blacklist_ todos os endereços Web podem ser acessados, exceto os definidos no arquivo de entrada
    * No modo de operação _whitelist_ todos os endereços Web são bloqueados, exceto os definidos no arquivo de entrada
* Apresentar de forma interativa as seguintes informações referentes às requisições submetidas:
  * Endereço IP de origem (cliente), porta de origem (cliente), URL de destino, IP de destino (servidor Web), Porta de destino (servidor Web), tempo gasto para recuperar a página na Internet;
* Ao ser finalizado, o programa deverá apresentar as seguintes estatísticas:
  * Lista contendo os endereços acessados (URLs) e suas respectivas frequências de acesso. A lista deverá estar ordenada de forma decrescente pelo número de acessos. Também deverá ser apresentado o tempo médio gasto pelo servidor proxy para recuperar cada endereço.
  * Lista contendo o endereço IP dos clientes que utilizaram o proxy. Para cada cliente deverá ser apresentada a quantidade de acessos submetidos. A lista deverá estar ordenada de forma decrescente da quantidade de acessos.
  * Lista contendo os endereços URL que tiveram tentativa de acesso e foram bloqueados. Deve ser apresentado o endereço IP do cliente que requisitou o acesso. Cada endereço IP deverá estar devidamente associado à URL bloqueada.
 
* Os endereços bloqueados deverão ter seu acesso negado. Ao negar um acesso, o servidor proxy deverá retornar ao cliente uma página HTML com a seguinte mensagem:

    "Página bloqueada pelo administrador da rede. Favor entrar em contato com a administração."
  * Importante: As páginas bloqueadas deverão ser recuperadas na Internet (pelo servidor proxy) e armazenadas em pastas específicas. O diretório de armazenamento deverá ser um parâmetro de inicialização do proxy. O armazenamento destas páginas tem como objetivo a consulta posterior do conteúdo que o usuário tentou acessar.
  * Os endereços liberados para acesso deverão ser acessados pelo servidor proxy e seus respectivos conteúdos devem ser encaminhados para o browser cliente, de forma que a página seja exibida corretamente.
* O arquivo _blacklist/whitelist_ deverá ser fornecido como parâmetro de inicialização do servidor.
  * Este arquivo deve ser um arquivo de texto simples, contendo um endereço URL por linha.


# Utilização

Este programa foi escrito em Java, utilizando Maven, portanto é necessário ter uma versão recente do Java (1.7+ e do Maven 3.x) instalados no servidor onde o proxy deve rodar.

Para gerar o artefato .jar que será utilizado, é necessário rodar o seguinte comando no terminal:

``` mvn clean compile assembly:single ```

o .jar deve rodar com as seguintes especificações:

``` java -jar proxy.jar <-b ou -w> <black or white list> <porta do proxy> <blocked pages dir> ```

* A opção __-b__ trata a lista como blacklist
* A opção __-w__ trata a lista como whitelist
  * os modos __-b__ e __-w__ são mutuamente excludentes
* O parâmetro __<black or white list>__ deve ser o path para um arquivo texto com a _blacklist_ ou _whitelist_ dependendo do modo como o proxy deve rodar
* O parâmetro __<porta do proxy>__ deve ser um número inteiro, e simboliza a porta que o proxy escutará as requisições.
* O parâmetro __<blocked pages dir>__ deve ser o path onde as páginas bloqueadas serão armazenadas para consulta futura.
  * Este path não deve terminar em /, mesmo simbolizando um diretório.


## Considerações Finais
Este trabalho foi produzido como um projeto de disciplina, e apesar de funcionar de acordo com os requisitos acima, não foi desenhado para servir como uma solução de proxy para situações de centenas ou milhares de requisições simultâneas (nunca foi testado sequer com dezenas).

Algumas soluções não implementadas que poderiam melhorar o desempenho do proxy são:
* Armazenar a _blacklist_ ou _whitelist_ em um Hashmap ou árvore binária de busca, acelerando a pesquisa da URL (Também é interessante padronizar as urls armazenadas, removendo o "www" e tudo posterior à primeira barra ("/"), fazendo o mesmo no momento da pesquisa )
* Melhorar a forma de se obter uma conexão com o banco de dados (criar uma factory ou manter um pool de conexões)
 
Qualquer melhoria implementada, independente de estar listada acima, pode ser enviada como pull request. Outras melhorias podem ser sugeridas como issues.

# Licensa
Este software é livre para uso, sem custos, com os seguintes requisitos:
* A atribuição deste software é obrigatória;
* Os comentários em formato Javadoc não podem ser removidos;
* Caso os comentários Javadoc sejam alterados, é __obrigatório__ que meu apelido (DRA2840) seja mantido como 1º autor, da forma como está atualmente (foto e link para o github);
* É proibida a cópia de qualquer trecho de código, alterando ou não o nome de métodos e variáveis, sem a devida atribuição.
