/* ***************************************************************
* Autor............: Antonio Vinicius Silva Dutra
* Matricula........: 202110810
* Inicio...........: 27/08/2024
* Ultima alteracao.: 08/08/2024
* Nome.............: mainControl.java
* Funcao...........: classe responsavel pelo controle da interface grafica.
Gerencia as imagens, botoes, adiciona os roteadores na interface, altera entre telas, etc.
****************************************************************/
package control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import model.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;

public class mainControl implements Initializable{
  @FXML private ImageView helpScreen;
  @FXML private ImageView aboutScreen;
  @FXML private ImageView background;
  @FXML private ImageView screen;
  @FXML private ImageView menorCaminhoIMG;

  @FXML private ImageView node;
  @FXML private ImageView nodeSent;
  @FXML private ImageView nodeReceive;

  @FXML private ImageView helpButton;
  @FXML private ImageView aboutButton;
  @FXML private ImageView closeButton;
  @FXML private ImageView sendButton;
  @FXML private ImageView iniciar;
  @FXML private ImageView resetButton;
  @FXML private ImageView startButton;
  @FXML private ImageView selectSender;
  @FXML private ImageView selectReceiver;

  @FXML private Label totalPackages;
  @FXML private Label senderId;
  @FXML private Label receiverId;

  @FXML private Text caminho;

  ArrayList<String> graph = new ArrayList<>(); //roteadores do Grafo para leitura do txt e implementacao visual
  ArrayList<Nodes> nodes = new ArrayList<>(); //roteadores
  ArrayList<ImageView> nodeImage = new ArrayList<>(); //imagem dos roteadores
  ArrayList<Polyline> lines = new ArrayList<>(); //linha visual que conecta os roteadores (rota)
  ArrayList<Label> numbers = new ArrayList<>(); //numero dos Roteadores
  ArrayList<Label> pesoConexoes = new ArrayList<>(); // Numero dos Roteadores

  int versionSelected = 0;
  int TTL = 1;
  int nodeSender = -1;
  int nodeReceiver = -1;
  int totalPackets = 0;
  
  boolean received = false;
  boolean graphFlag = true;

  Pane root = new Pane();
  ColorAdjust colorAdjust = new ColorAdjust();

  /* ******************************************************************
  * Metodo: initialize()
  * Funcao: Inicializa a interface grafica, configura efeitos nos botoes e define valores iniciais para componentes como Spinner.
  * Parametros: 
  - URL location: Localizacao usada para resolver caminhos relativos para o objeto raiz, ou null se nao for conhecido
  - ResourceBundle resources: Recurso para localizar o objeto raiz, ou null se o objeto raiz nao tiver sido localizado.
  * Retorno: void
  ****************************************************************** */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    buttonEffects();

    setOffOn(menorCaminhoIMG, 0);
    setOffOn(iniciar, 1);
    setOffOn(startButton, 0);
    setOffOn(selectSender, 0);
    setOffOn(selectReceiver, 0);
    setOffOn(screen, 0);

  }
    
  /* ******************************************************************
  * Metodo: readBackbone()
  * Funcao: le o arquivo backbone.txt e armazena cada linha no ArrayList graph. 
  Verifica se o arquivo eh valido e caso nao seja valido, dispara uma mensagem de erro
  * Parametros: Nenhum
  * Retorno: boolean - true se o arquivo foi lido com sucesso, false se ocorreu um erro.
  ****************************************************************** */
  public boolean readBackbone(){
    String backbone = "./backbone.txt";
    try {
      File file = new File(backbone);
      //apenas um print caso o arquivo seja valido ou nao
      if(file.exists()){
        System.out.println("Backbone valido!");
      } else {
        System.out.println("Backbone nao existe");
      }

      //bufferedreader responsavel pela leitura do arquivo
      BufferedReader reader = new BufferedReader(new FileReader(backbone));
      System.out.println("Arquivo lido com sucesso!");
      
      String line; //armazena cada linha do arquivo
      while ((line = reader.readLine()) != null) { //le cada linha do arquivo
        graph.add(line);
      }
      reader.close();
      return true;

    } catch (IOException e) {
      showAlert("Aviso!", "Ocorreu um erro ao ler o arquivo!");
      return false;
    } catch (NumberFormatException e) {
      showAlert("Aviso!", "Ocorreu um erro ao ler o arquivo!");
      return false;
    }
  }

  /* ******************************************************************
  * Metodo: addNode
  * Funcao: Adiciona nos / roteadores na interface grfica. Verifica se o numero de roteadores eh valido antes de prosseguir
  * Parametros: Pane root: O painel raiz onde os componentes graficos serao adicionados.
  * Retorno: void
  ****************************************************************** */
  public void addNode(Pane root) {
    int totalNodes = Integer.parseInt(graph.get(0).replaceAll(";", ""));
    System.out.println("Numero de roteadores totais: " + totalNodes);

    if (totalNodes < 21) {
      ArrayList<Circle> routers = createCircles(root, totalNodes);
      createLabels(root, routers);
      createConnections(root, routers);
      assembleGraph(root, routers);
      changeScreen();
      selectFirstNode();
    } else {
      showAlert("Erro!", "O número de roteadores deve ser menor ou igual a 20!");
    }
  }

  /* ******************************************************************
  * Metodo: createCircles
  * Funcao: Cria circulos (imagens) que representam os roteadores na interface grafica e os posiciona em forma de circulo ao redor do painel raiz
  * Parametros: 
    - Pane root: O painel onde os circulos serao adicionados
    - int numCircles: O numero total de roteadores (circulos) a serem criados
  * Retorno: ArrayList<Circle> - Lista de circulos que representam os roteadores
****************************************************************** */
  private ArrayList<Circle> createCircles(Pane root, int numCircles) {
    ArrayList<Circle> routers = new ArrayList<>();
    double centerX = root.getWidth() / 2;
    double centerY = (root.getHeight() / 2) + 20;
    double radius = 210;
    double angleIncrement = 2 * Math.PI / numCircles;

    for (int i = 0; i < numCircles; i++) {
      double angle = -Math.PI / 2 + i * angleIncrement;
      double x = centerX + radius * Math.cos(angle);
      double y = centerY + radius * Math.sin(angle);

      Circle node = new Circle(x, y, 25);
      routers.add(node);

      Nodes router = new Nodes(i + 1);
      router.setController(this);
      nodes.add(router);
    }
    //createConnections(root, routers);
    return routers;
  }

  /* ******************************************************************
   Metodo: createLabels
  * Funcao: Cria rotulos para cada roteador, mostrando o nomero do roteador proximo a imagem correspondente a ele na interface
  * Parametros: 
    - Pane root: O painel onde os rotulos serao adicionados
    - ArrayList<Circle> routers: Lista de circulos que representam os roteadores
  * Retorno: void
  ****************************************************************** */
  private void createLabels(Pane root, ArrayList<Circle> routers) {
    for (int i = 0; i < routers.size(); i++) {
        Circle node = routers.get(i);
        double labelX = node.getCenterX() - 12;
        double labelY = node.getCenterY() + 28;

        char letter = (char) (i + 65);

        Label label = new Label(Character.toString(letter));
        label.setLayoutX(labelX);
        label.setLayoutY(labelY);
        label.setStyle("-fx-font-size: 6pt; " + "-fx-font-weight: bold;");
        label.setTextFill(Color.BLACK);
        numbers.add(label);
    }
  }

  /* ******************************************************************
  * Metodo: createConnections
  * Funcao: Cria linhas que conectam os roteadores na interface grafica, representando as conexoes entre eles
  * Parametros: 
    - Pane root: O painel onde as conexoes (linhas) serao adicionadas
    - ArrayList<Circle> routers: Lista de circulos que representam os roteadores
  * Retorno: void
  ****************************************************************** */
  private void createConnections(Pane root, ArrayList<Circle> routers) {
    for (String line : graph) {
        String[] parts = line.split(";");
        if (parts.length >= 2) {
          int nodeOne = Integer.parseInt(parts[0]);
          int nodeTwo = Integer.parseInt(parts[1]);
          int peso = Integer.parseInt(parts[2]);

          // Criando a linha de conexao entre dois roteadores
          Polyline Line = new Polyline(
            routers.get(nodeOne - 1).getCenterX(), routers.get(nodeOne - 1).getCenterY(),
            routers.get(nodeTwo - 1).getCenterX(), routers.get(nodeTwo - 1).getCenterY()
          );
          lines.add(Line);

          // Calculo da posicao do peso na tela (meio da linha)
          double xPeso = (routers.get(nodeOne - 1).getCenterX() + routers.get(nodeTwo - 1).getCenterX()) / 2;
          double yPeso = (routers.get(nodeOne - 1).getCenterY() + routers.get(nodeTwo - 1).getCenterY()) / 2;

          // Criando o label para exibir o peso
          Label pesoTela = new Label(Integer.toString(peso));
          pesoTela.setLayoutX(xPeso);
          pesoTela.setLayoutY(yPeso);
          pesoConexoes.add(pesoTela);

          // Adicionando a conexao entre os roteadores com a informacao do peso
          nodes.get(nodeOne - 1).addConnection(nodeTwo, Line, pesoTela);
          nodes.get(nodeTwo - 1).addConnection(nodeOne, Line, pesoTela);
        }
    }
    //assembleGraph(root, routers);
  }

  /* ******************************************************************
  * Metodo: assembleGraph
  * Funcao: Adiciona os circulos, linhas e rotulos ao painel raiz para formar o grafo visual que representa a rede de roteadores
  * Parametros: 
    - Pane root: O painel onde os elementos do grafo serao adicionados
    - ArrayList<Circle> routers: Lista de circulos que representam os roteadores
  * Retorno: void
  ****************************************************************** */
  private void assembleGraph(Pane root, ArrayList<Circle> routers) {
    for (Polyline Line : lines) {
        Line.setStroke(Color.BLACK);
        Line.setStrokeWidth(2);
        root.getChildren().add(Line);
    }

    for (Circle circle : routers) {
        ImageView firstNode = new ImageView(new Image("./imgs/node.png"));
        firstNode.setLayoutX(circle.getCenterX() - circle.getRadius());
        firstNode.setLayoutY(circle.getCenterY() - circle.getRadius());
        nodeImage.add(firstNode);
        root.getChildren().add(firstNode);
    }

    for (Label label : numbers) {
        label.setStyle("-fx-font-size: 12pt; " + "-fx-font-weight: bold;");
        root.getChildren().add(label);
    }

    // Adiciona os numeros do peso na tela
    for (Label label : pesoConexoes) {
        label.setStyle("-fx-font-size: 10pt; " + "-fx-font-weight: bold;");
        label.setTextFill(Color.WHITE);
        root.getChildren().add(label);
    }

    for (int i = 0; i < nodes.size(); i++) {
        nodes.get(i).listConnections();
    }
}

  /* ******************************************************************
  * Metodo: selectFirstNode
  * Funcao: Permite que o usuurio selecione o roteador transmissor na interface, desativando o clique apos a selecao.
  * Parametros: Nenhum
  * Retorno: void
  ****************************************************************** */
  public void selectFirstNode() {
    for (int i = 0; i < nodeImage.size(); i++) {
      nodeImage.get(i).setCursor(Cursor.HAND);
      final int posicao = i; // armazena a posicao atual do loop
      nodeImage.get(i).setOnMouseClicked(event -> {
        int id = posicao+1;
        System.out.println("Transmissor [ " + id + " ] selecionado."); // informa o id do roteador escolhido

        setNodeInicial(id); // setando o valor do roteador transmissor
        nodeImage.get(posicao).setImage(new Image("./imgs/nodeSender.png"));
        // abaixo remove o evento de clique de todas as imagens
        for (ImageView imageView : nodeImage) {
          imageView.setOnMouseClicked(null);
          imageView.setCursor(null);
        }
        //seta na label da interface qual o id do transmissor
        senderId.setText(Integer.toString(getNodeSender()));

        setOffOn(selectSender, 0);
        selectFinalNode();
      });
    }
  }

  /* ******************************************************************
  * Metodo: selectFinalNode
  * Funcao: Permite que o usuurio selecione o roteador receptor na interface , desativando o clique apos a selecao
  * Parametros: Nenhum
  * Retorno: void
  ****************************************************************** */
  public void selectFinalNode() {
    selectReceiver.setVisible(true);

    for (int i = 0; i < nodeImage.size(); i++) {
      if(i != getNodeSender()-1){ // todos os roteadores menos o transmissor
        nodeImage.get(i).setCursor(Cursor.HAND);
        final int posicao = i; // armazena a posicao atual do loop
        nodeImage.get(i).setOnMouseClicked(event -> {
          int id = posicao + 1;
          System.out.println("Receptor [ " + id + " ] selecionado."); //informa o id do roteador escolhido

          setNodeFinal(id); // setando o valor do roteador receptor
          nodeImage.get(posicao).setImage(new Image("./imgs/nodeReceiver.png"));
          // abaixo remove o evento de clique de todas as imagens
          for (ImageView imageView : nodeImage) {
            imageView.setOnMouseClicked(null);
            imageView.setCursor(null);
          }

          //seta na label da interface qual o id do receptor
          receiverId.setText(Integer.toString(getNodeReceiver()));

          setOffOn(selectReceiver, 0);
          setOffOn(startButton, 1);
        });
      }
    }
  }

  @FXML
  void opcaoSelecionada(MouseEvent event) {
    setOffOn(screen, 1);
    setOffOn(iniciar, 0);
    startProgram();
  }

  /* ******************************************************************
  * Metodo: showAlert
  * Funcao: Exibe uma caixa de dialogo de alerta com uma mensagem personalizada (geralmente de erro)
  * Parametros: 
    - String title: O titulo da caixa de dialogo.
    - String header: A mensagem exibida na caixa de dialogo.
  * Retorno: void
  ****************************************************************** */
  public void showAlert(String title, String header) {
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.showAndWait();
  }

  /* ******************************************************************
  * Metodo: clickStart
  * Funcao: inicia a transmissao depois que o transmissor e o receptor forem escolhidos
  * Parametros: 
    - Mouse event -> clique do mouse no botao
  * Retorno: void
  ****************************************************************** */
  @FXML
  void clickStart(MouseEvent event) {
    if (nodeReceiver != -1 && nodeSender != -1) {
      nodes.get(nodeSender - 1).calcularMenorCaminho(); // Chama o Metodo para Calcular o Menor Caminho
      setOffOn(startButton, 0);
      setOffOn(menorCaminhoIMG, 1);
    } else {
     showAlert("Erro!","Selecione o Transmissor e/ou Receptor");
    }
  }

   /* ******************************************************************
  * Metodo: aboutButton
  * Funcao: mostra a tela com as informacoes sobre a versao 4
  * Parametros: 
    - Mouse event -> clique do mouse no botao
  * Retorno: void
  ****************************************************************** */
  @FXML
  void aboutButton(MouseEvent event) {
    setOffOn(aboutScreen, 1);
    setOffOn(closeButton, 1);
  }

  /* ******************************************************************
  * Metodo: closeButton
  * Funcao: fecha a tela com as informacoes sobre a versao 4
  * Parametros: 
    - Mouse event -> clique do mouse no botao
  * Retorno: void
  ****************************************************************** */
  @FXML
  void closeButton(MouseEvent event) {
    setOffOn(aboutScreen, 0);
    setOffOn(closeButton, 0);
  }

  /* ******************************************************************
  * Metodo: reset
  * Funcao: eh um botao que chama o metodo resetVariables para fazer o programa voltar ao seu estado inicial.
  Desliga todos os botoes da tela de execucao e liga novamente os botoes e imagens do menu principal
  * Parametros: 
    - Mouse event -> clique do mouse no botao
  * Retorno: void
  ****************************************************************** */
  @FXML
  void reset(MouseEvent event) {
    removeGraphs();
    for(int i = 0; i < nodes.size(); i++){ 
      nodes.get(i).stopPackages(); //para a animacao dos pacotes
    }

    resetVariables();

    setOffOn(menorCaminhoIMG, 0);
    setOffOn(background, 1);
    setOffOn(screen, 0);
    setOffOn(startButton, 0);
    setOffOn(resetButton, 0);
    setOffOn(selectReceiver, 0);
    setOffOn(selectSender, 0);
    setOffOn(iniciar, 1);

    Nodes routers = new Nodes();
    routers.resetRoutingTable();
  }

  /* ******************************************************************
  * Metodo: resetVariables
  * Funcao: reseta todas as variaveis, threads e imagens
  * Parametros: 
    - Mouse event -> clique do mouse no botao
  * Retorno: void
  ****************************************************************** */
  public void resetVariables(){
    graph = new ArrayList<>();
    nodes = new ArrayList<>();
    nodeImage = new ArrayList<>();
    lines = new ArrayList<>();
    numbers = new ArrayList<>(); 
    versionSelected = 0;
    TTL = -1;
    nodeSender = -1;
    nodeReceiver = -1;
    totalPackets = 0;
    graphFlag = true;
    received = false;
    totalPackages.setText(null);
    receiverId.setText(null);
    senderId.setText(null);
  }

  /* ******************************************************************
  * Metodo: buttonEffects
  * Funcao: aplica efeitos de brilho nos botoes da interface
  * Parametros: Nenhum
  * Retorno: void
  ****************************************************************** */
  public void buttonEffects(){
    colorAdjust.setBrightness(1);


    startButton.setOnMouseEntered(event -> {
      startButton.setEffect(colorAdjust);
    });

    startButton.setOnMouseExited(event -> {
      startButton.setEffect(null);
    });

    helpButton.setOnMouseEntered(event -> {
      helpScreen.setVisible(true);
    });

    helpButton.setOnMouseExited(event -> {
      helpScreen.setVisible(false);
    });

    resetButton.setOnMouseEntered(event -> {
      resetButton.setEffect(colorAdjust);
    });

    resetButton.setOnMouseExited(event -> {
      resetButton.setEffect(null);
    });

    iniciar.setOnMouseEntered(event -> {
      iniciar.setEffect(colorAdjust);
    });

    iniciar.setOnMouseExited(event -> {
      iniciar.setEffect(null);
    });
  }

  /* ******************************************************************
  * Metodo: removeGraphs
  * Funcao: remove os grafos da tela
  * Parametros: Nenhum
  * Retorno: void
  ****************************************************************** */
  public void removeGraphs(){
    for (ImageView image : nodeImage) {
      getRoot().getChildren().remove(image);
    }
    for (Polyline Line : lines) {
      getRoot().getChildren().remove(Line);
    }
    for (Label label : numbers) {
      getRoot().getChildren().remove(label);
    }
    for (Label label : pesoConexoes){
      getRoot().getChildren().remove(label);
    }
  }

  /* ******************************************************************
  * Metodo: addPackets
  * Funcao: incrementa o numero total de pacotes gerados e seta o valor deles na interface
  * Parametros: Nenhum
  * Retorno: void
  ****************************************************************** */
  public void addPackets(){ 
    if(!received){
      totalPackets++;
      totalPackages.setText(Integer.toString(totalPackets));
    }
  }

  /* ******************************************************************
  * Metodo: packetReceived
  * Funcao: troca a imagem do roteador receptor caso ele tenha recebido o pacote
  * Parametros: 
    - Int node -> informe o roteador que recebeu o pacote
  * Retorno: void
  ****************************************************************** */
  public void packetReceived(int node){
    nodeImage.get(node-1).setImage(new Image("./imgs/packetReceived.png"));
  }

  public void encontrarMenorDistancia() {
    
    boolean todosVisitados = false; // verifica se todos nos ja foram visitados ou nao
    int menorValor = 99999;// Assume um valor Exorbitante como o Menor Inicialmente
    int indiceMenorValor = 0; // Armazena o índice do menor valor

    // Percorre a lista para encontrar o menor valor e seu índice
    for (int i = 0; i < nodes.size(); i++) {
      if(!nodes.get(i).getVisitado()){
        todosVisitados = true;
        int valorAtual = nodes.get(i).getDistancia();
        if (valorAtual < menorValor && valorAtual != -1) {
          menorValor = valorAtual;
          indiceMenorValor = i;
        }
      }
    }
    if(todosVisitados){
      nodes.get(indiceMenorValor).calcularMenorCaminho(); //Chama o roteador com o Menor Caminho
    }
    else{
      for (Nodes x : nodes) {
        System.out.println("ID DO ROTEADOR [ "+ x.getId() + " ] Sua Distancia do No Inicial [ "+ x.getDistancia()+" ]"+ " Seu Predecessor [ "+ x.getPredecessor() + " ]");
      }
      UpdateGUImostrarMenorCaminho();
      
    }
}

public void UpdateGUImostrarMenorCaminho() {
  ArrayList<Integer> auxGUI = new ArrayList<>();

  StringBuilder menorCaminho = new StringBuilder();
  int nodePai = nodeReceiver - 1;

  // Construindo o menor caminho
  while (nodeSender - 1 != nodePai) {
      // Converte o predecessor para uma letra (ASCII)
      char predecessorChar = (char) (nodes.get(nodePai).getPredecessor() + 64);
      menorCaminho.insert(0, " -> " + predecessorChar);
      nodePai = nodes.get(nodePai).getPredecessor() - 1;
      auxGUI.add(nodePai);
  }
  
  // Adiciona o nó receptor como a última letra
  menorCaminho.append(" -> " + (char) (nodeReceiver + 64));
  menorCaminho.delete(0, 4);  // Remove o primeiro " -> "

  System.out.println("Menor caminho: " + menorCaminho.toString());

  auxGUI.add(nodeReceiver - 1);

  // Ajusta a opacidade das imagens de nós não pertencentes ao menor caminho
  for (int i = 0; i < nodeImage.size(); i++) {
      ImageView imageView = nodeImage.get(i);  // Obtém a ImageView atual
      if (!auxGUI.contains(i)) {
          imageView.setOpacity(0.15);  // Ajusta a opacidade da imagem
      }
  }

  // Ajusta a opacidade das labels de nós não pertencentes ao menor caminho
  for (int i = 0; i < numbers.size(); i++) {
      Label label = numbers.get(i);  // Obtém a Label atual
      if (!auxGUI.contains(i)) {
          label.setOpacity(0.15);  // Ajusta a opacidade da label
      }
  }

  // Ajusta a opacidade dos roteadores na GUI
  for (Nodes router : nodes) {
      router.setOpacityGui();
  }

  // Divide o menor caminho em uma lista de letras
  String[] numerosString = menorCaminho.toString().split(" -> ");
  ArrayList<Integer> numeros = new ArrayList<>();
  
  for (String numeroString : numerosString) {
      // Converte a letra de volta para o número correspondente
      int numero = numeroString.charAt(0) - 64;
      numeros.add(numero);
  }

  // Ajusta a visualização das conexões
  for (int i = 0; i < numeros.size() - 1; i++) {
      ArrayList<Integer> nos = nodes.get(numeros.get(i) - 1).getNodeConnection();
      int indiceEncontrado = -1;  // Inicializa com -1 para indicar que nenhum índice foi encontrado
      for (int j = 0; j < nos.size(); j++) {
          if (nos.get(j).equals(numeros.get(i + 1))) {
              indiceEncontrado = j;
              break;  // Se encontrar o índice, não é necessário continuar procurando
          }
      }
      nodes.get(numeros.get(i) - 1).ajusteVisualizacaoGUI(indiceEncontrado);
  }

  // Mostra o caminho final em ASCII na interface
  menorCaminhoIMG.setVisible(true);
  menorCaminhoIMG.setDisable(false);
  caminho.setText(menorCaminho.toString());
  caminho.setVisible(true);
  caminho.setDisable(false);

  // Envia os pacotes no caminho
  nodes.get(nodeSender - 1).sendPackets(numeros);
}

  /* ******************************************************************
  * Metodo: startProgram()
  * Funcao: responsavel por iniciar o programa de fato nas versoes 3 e 4.
  Verifica se a leitura do backbone teve sucesso e assim, inicia o programa
  * Parametros: Nenhum
  * Retorno: void
  ****************************************************************** */
  public void startProgram(){
    if(readBackbone()){
      addNode(root);
    };
  }

  /* ******************************************************************
  * Metodo: settOffOn
  * Funcao: Desliga ou liga imagens/botoes do tipo imageView, conforme parametro.
  * Parametros: 
    - ImageView image -> a imagem que vai ser ligada ou desligada
    - Int turn -> Informa se vai ser ligada ou desligada dependendo do seu valor
  * Retorno: void
  ****************************************************************** */
  @FXML
  public void setOffOn(ImageView image, int turn){
    if (turn == 0){
      image.setVisible(false);
      image.setDisable(true);
    } else {
      image.setVisible(true);
      image.setDisable(false);
    }

  }

  /* ******************************************************************
  * Metodo: changeScreen
  * Funcao: realiza a troca de telas utilizando o metodo setOffOn
  * Parametros: Nenhum
  * Retorno: void
  ****************************************************************** */
   public void changeScreen() {
    setOffOn(background, 0);
    setOffOn(selectSender, 1);
    setOffOn(resetButton, 1);
  }

  //getters and setters
  public ArrayList<Nodes> getNodes() {
    return nodes;
  }

  public void setPane(Pane pane){
    root = pane;
  }

  public void setReceived(boolean received) {
    this.received = received;
  }

  public Pane getRoot(){
    return root;
  }

  public void setVersionSelected(int versionSelected){
    this.versionSelected = versionSelected;
  }

  public int getVersionSelected(){
    return versionSelected;
  }

  public void setNodeFinal(int nodeFinal) {
    this.nodeReceiver = nodeFinal;
  }

  public void setNodeInicial(int nodeInicial) {
    this.nodeSender = nodeInicial;
  }

  public int getNodeReceiver() {
    return nodeReceiver;
  }

  public int getNodeSender() {
    return nodeSender;
  }
}
