/* ***************************************************************
* Autor............: Antonio Vinicius Silva Dutra
* Matricula........: 202110810
* Inicio...........: 20/09/2024
* Ultima alteracao.: 21/09/2024
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
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;

public class mainControl implements Initializable{
  @FXML private ImageView background;
  @FXML private ImageView screen;
  @FXML private ImageView shorterPathImg;
  @FXML private ImageView aboutScreen;

  @FXML private ImageView node;
  @FXML private ImageView nodeSent;
  @FXML private ImageView nodeReceive;

  @FXML private ImageView start;
  @FXML private ImageView resetButton;
  @FXML private ImageView aboutButton;
  @FXML private ImageView startButton;
  @FXML private ImageView selectSender;
  @FXML private ImageView selectReceiver;

  @FXML private Label senderId;
  @FXML private Label receiverId;

  @FXML private Text pathText;
  @FXML private Text pathCostText;

  ArrayList<String> graph = new ArrayList<>(); //roteadores do Grafo para leitura do txt e implementacao visual
  ArrayList<Nodes> nodes = new ArrayList<>(); //roteadores
  ArrayList<ImageView> nodeImage = new ArrayList<>(); //imagem dos roteadores
  ArrayList<Polyline> lines = new ArrayList<>(); //linha visual que conecta os roteadores (rota)
  ArrayList<Label> numbers = new ArrayList<>(); //numero dos Roteadores
  ArrayList<Label> conectionCost = new ArrayList<>(); // Numero dos Roteadores

  int nodeSender = -1;
  int nodeReceiver = -1;
  
  boolean received = false;
  boolean graphFlag = true;

  Pane root = new Pane();
  ColorAdjust color = new ColorAdjust();

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

    setOffOn(shorterPathImg, 0);
    setOffOn(start, 1);
    setOffOn(startButton, 0);
    setOffOn(selectSender, 0);
    setOffOn(selectReceiver, 0);
    setOffOn(screen, 0);

  }//fim do metodo initialize()
    
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
  }//fim do metodo readBackbone()

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
  }//fim do metodo addNode()

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
  }//fim do metodo createCircles()

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
      DropShadow ds = new DropShadow();
      double labelX = node.getCenterX() - 10;
      double labelY = node.getCenterY() - 5;

      char letter = (char) (i + 65);

      Label label = new Label(Character.toString(letter));
      label.setLayoutX(labelX);
      label.setLayoutY(labelY);
      label.setStyle("-fx-font-size: 6pt; " + "-fx-font-weight: bold;");
      label.setTextFill(Color.WHITE);
      ds.setColor(Color.BLACK);
      ds.setOffsetX(0);
      ds.setOffsetY(0);
      ds.setRadius(3);
      label.setEffect(ds); 

      numbers.add(label);
    }
  }//fim do metodo createLabels

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

        //linha que conecta dois roteadores
        Polyline Line = new Polyline(
          routers.get(nodeOne - 1).getCenterX(), routers.get(nodeOne - 1).getCenterY(),
          routers.get(nodeTwo - 1).getCenterX(), routers.get(nodeTwo - 1).getCenterY()
        );
        lines.add(Line);

        //calculo de onde colocar o custo do caminho na interface
        double xCost = (routers.get(nodeOne - 1).getCenterX() + routers.get(nodeTwo - 1).getCenterX()) / 2;
        double yCost = (routers.get(nodeOne - 1).getCenterY() + routers.get(nodeTwo - 1).getCenterY()) / 2;

        //label que vai exibir o custo do caminho
        Label pathCost = new Label(Integer.toString(peso));
        pathCost.setLayoutX(xCost);
        pathCost.setLayoutY(yCost);
        conectionCost.add(pathCost);

        //adicionando a conexao entre os roteadores com a informacao do peso
        nodes.get(nodeOne - 1).addConnection(nodeTwo, Line, pathCost);
        nodes.get(nodeTwo - 1).addConnection(nodeOne, Line, pathCost);
      }
    }
  }//fim do metodo createConnections

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

    // adiciona o custo na tela
    for (Label label : conectionCost) {
      label.setStyle("-fx-font-size: 10pt; " + "-fx-font-weight: bold;");
      label.setTextFill(Color.WHITE);
      root.getChildren().add(label);
    }
  }//fim do metodo assembleGraph()

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
        char letterAux = (char) (id + 64);
        System.out.println("Transmissor [ " + Character.toString(letterAux) + " ] selecionado."); // informa o id do roteador escolhido

        setNodeInicial(id); // setando o valor do roteador transmissor
        nodeImage.get(posicao).setImage(new Image("./imgs/nodeSender.png"));
        // abaixo remove o evento de clique de todas as imagens
        for (ImageView imageView : nodeImage) {
          imageView.setOnMouseClicked(null);
          imageView.setCursor(null);
        }
        //seta na label da interface qual o id do transmissor
        char letter = (char) (getNodeSender() + 64);
        senderId.setText(Character.toString(letter));

        setOffOn(selectSender, 0);
        selectFinalNode();
      });
    }
  }//fim do metodo selectFirstNode()

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
          char letterAux = (char) (id + 64);
          System.out.println("Receptor [ " + Character.toString(letterAux) + " ] selecionado.\n"); //informa o id do roteador escolhido

          setNodeFinal(id); // setando o valor do roteador receptor
          nodeImage.get(posicao).setImage(new Image("./imgs/nodeReceiver.png"));
          // abaixo remove o evento de clique de todas as imagens
          for (ImageView imageView : nodeImage) {
            imageView.setOnMouseClicked(null);
            imageView.setCursor(null);
          }

          //seta na label da interface qual o id do receptor
          char letter = (char) (getNodeReceiver() + 64);
          receiverId.setText(Character.toString(letter));

          setOffOn(selectReceiver, 0);
          setOffOn(startButton, 1);
        });
      }
    }
  }//fim do metodo selectFinalNode()

  /* ******************************************************************
  * Metodo: opcaoSelecionada(MouseEvent event)
  * Funcao: inicia o programa e desliga a tela inicial
  * Parametros: 
    - MouseEvent event: Clique do mouse no botao
  * Retorno: void
  ****************************************************************** */
  @FXML
  void opcaoSelecionada(MouseEvent event) {
    setOffOn(screen, 1);
    setOffOn(start, 0);
    setOffOn(aboutButton, 0);
    startProgram();
  }//fim do metodo opcaoSelecionada

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
  }//fim do metodo shorAlert()

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
      nodes.get(nodeSender - 1).getShortestPath(); // chama o Metodo pra calcular o menor Caminho
      setOffOn(startButton, 0);
      setOffOn(shorterPathImg, 1);
    } else {
     showAlert("Erro!","Selecione o Transmissor e/ou Receptor");
    }
  }//fim do metodo clickStart()


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

    setOffOn(shorterPathImg, 0);
    setOffOn(background, 1);
    setOffOn(screen, 0);
    setOffOn(startButton, 0);
    setOffOn(resetButton, 0);
    setOffOn(selectReceiver, 0);
    setOffOn(selectSender, 0);
    setOffOn(start, 1);
    setOffOn(aboutButton, 1);
  }//fim do metodo reset()

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
    nodeSender = -1;
    nodeReceiver = -1;
    graphFlag = true;
    received = false;
    receiverId.setText(null);
    senderId.setText(null);
    pathText.setText(null);
    pathCostText.setText(null);
  }//fim do metodo resetVariables()

  /* ******************************************************************
  * Metodo: buttonEffects
  * Funcao: aplica efeitos de brilho nos botoes da interface
  * Parametros: Nenhum
  * Retorno: void
  ****************************************************************** */
  public void buttonEffects(){
    color.setBrightness(0.5);

    start.setOnMouseEntered(event -> {
      start.setEffect(color);
    });

    start.setOnMouseExited(event -> {
      start.setEffect(null);
    });

    startButton.setOnMouseEntered(event -> {
      startButton.setEffect(color);
    });

    startButton.setOnMouseExited(event -> {
      startButton.setEffect(null);
    });

    resetButton.setOnMouseEntered(event -> {
      resetButton.setEffect(color);
    });

    resetButton.setOnMouseExited(event -> {
      resetButton.setEffect(null);
    });

    aboutButton.setOnMouseEntered(event -> {
      aboutScreen.setVisible(true);
    });

    aboutButton.setOnMouseExited(event -> {
      aboutScreen.setVisible(false);
    });
  }//fim do metodo buttonEffects()

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
    for (Label label : conectionCost){
      getRoot().getChildren().remove(label);
    }
  }//fim do metodo removeGraphs()


  /* ******************************************************************
  * Metodo: packetReceived
  * Funcao: troca a imagem do roteador receptor caso ele tenha recebido o pacote
  * Parametros: 
    - Int node -> informe o roteador que recebeu o pacote
  * Retorno: void
  ****************************************************************** */
  public void packetReceived(int node){
    nodeImage.get(node-1).setImage(new Image("./imgs/packetReceived.png"));

    for (Label label : numbers) {
      root.getChildren().remove(label);
    }
  }//fim do metodo packetRecived()

  /* ******************************************************************
  * Metodo: findShorterPath()
  * Funcao: busca o roteador nao visitado com a menor distancia e chama sua funcao de menor caminho. 
  Se todos os roteadores ja foram visitados, ele constroi e exibe o menor caminho entre o roteador inicial e o destino, 
  ajustando a interface visual e enviando pacotes
  * Parametros: null
  * Retorno: void
  ****************************************************************** */
  public void findShorterPath() {
    int shortestDistance = Integer.MAX_VALUE; 
    int shortestNodeIndex = -1;
    int originRouter = nodeReceiver - 1;
    boolean unvisitedNodesExist = false;

    ArrayList<Integer> pathIndex = new ArrayList<>();
    StringBuilder pathBuilder = new StringBuilder();
    
    // percorre os roteadores para encontrar o menor caminho
    for (int i = 0; i < nodes.size(); i++) {
      Nodes currentNode = nodes.get(i);

      if (!currentNode.getVisited()) {
        unvisitedNodesExist = true;
        int distance = currentNode.getDistance();

        if (distance != -1 && distance < shortestDistance) {
          shortestDistance = distance;
          shortestNodeIndex = i;
        }
      }
    }

    // ce existirem roteadores nao visitados, chama o proximo com o menor caminho
    if (unvisitedNodesExist && shortestNodeIndex != -1) {
      nodes.get(shortestNodeIndex).getShortestPath();
    } else {
      //caso todos os roteadores ja tenham sido visitados, imprime as distancias no terminal
      for (Nodes node : nodes) {
        char nodeLabel = (char) (node.getId() + 64);
        System.out.println("Custo do roteador [" + nodeLabel + "] ate o inicial: " + node.getDistance());
      }
      
      //construindo o menor caminho em ASCII
      while (nodeSender - 1 != originRouter) {
        //converte o predecessor para uma letra ASCII
        char previous = (char) (nodes.get(originRouter).getPrevious() + 64);
        pathBuilder.insert(0, " -> " + previous);
        originRouter = nodes.get(originRouter).getPrevious() - 1;
        pathIndex.add(originRouter);
      }
  
      //adiciona o receptor no final
      char receiverLabel = (char) (nodeReceiver + 64);
      pathBuilder.append(" -> " + receiverLabel).delete(0, 4);

      System.out.println("\nMenor caminho: " + pathBuilder.toString());

      pathIndex.add(nodeReceiver - 1);

      updateOpacity(pathIndex);

      //divide o menor caminho em uma lista de letras em ascii
      String[] strings = pathBuilder.toString().split(" -> ");
      ArrayList<Integer> nums = new ArrayList<>();
  
      for (String stringNum : strings) {
        //agora converto o ascii para o numero correspondente
        int numConverted = stringNum.charAt(0) - 64;
        nums.add(numConverted);
      }

      for (int i = 0; i < nums.size() - 1; i++) {
        ArrayList<Integer> roteadores = nodes.get(nums.get(i) - 1).getNodeConnection();
        int indexFound = -1; 
        for (int j = 0; j < roteadores.size(); j++) {
          if (roteadores.get(j).equals(nums.get(i + 1))) {
              indexFound = j;
              break;  // se encontrar o indice, nao eh necessário continuar procurando
          }
        }
        nodes.get(nums.get(i) - 1).ajustInterface(indexFound);
      }
    
      showShortestPath(pathBuilder);
      nodes.get(nodeSender - 1).sendPackets(nums);//envia os pacotes
    }
  }//fim do metodo findShortestPath()

  /* ******************************************************************
  * Metodo: updateOpacity(ArrayList<Integer> pathIndex)
  * Funcao: ajusta a opacidade das imagens de roteadores nao pertencentes ao menor caminho
  * Parametros: pathIndex = index do caminho
  * Retorno: void
  ****************************************************************** */
  public void updateOpacity(ArrayList<Integer> pathIndex){
    for (int i = 0; i < nodeImage.size(); i++) {
      ImageView imageView = nodeImage.get(i);  //obtem a ImageView atual
      if (!pathIndex.contains(i)) {
        imageView.setOpacity(0.15);  //ajusta a opacidade da imagem
      }
    }

    for (int i = 0; i < numbers.size(); i++) {
      Label label = numbers.get(i); 
      if (!pathIndex.contains(i)) {
        label.setOpacity(0.15);
      }
    }

    for (Nodes router : nodes) {
      router.setOpacityInterface();
    }
  }//fim do metodo updateOpacity


  /* ******************************************************************
  * Metodo: showShortestPath(StringBuilder pathBuilder))
  * Funcao: mostra o caminho final em ASCII na interface
  * Parametros: StringBuilder pathBuilder = construtor do caminho em ascii
  * Retorno: void
  ****************************************************************** */
  public void showShortestPath(StringBuilder pathBuilder){
    shorterPathImg.setVisible(true);
    shorterPathImg.setDisable(false);
    pathText.setText(pathBuilder.toString());
    pathText.setVisible(true);
    pathText.setDisable(false);
    pathCostText.setText(String.valueOf(nodes.get(nodeReceiver-1).getDistance()));
  }//fim do metodo showShortestPath()

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
  }//fim do metodo startProgram()

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
  } //fim do metodo setOffOn()

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
    setOffOn(startButton, 1);
  }//fim do metodo changeScreen

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
