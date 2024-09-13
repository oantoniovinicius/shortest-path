/* ***************************************************************
* Autor............: Antonio Vinicius Silva Dutra
* Matricula........: 202110810
* Inicio...........: 27/08/2024
* Ultima alteracao.: 08/08/2024
* Nome.............: Packets.java
* Funcao...........: classe responsavel pelo gerenciamentos dos pacotes. 
Isso inclui as threads e as suas animacoes, assim como todas as variveis necessarias para
que o caminho entre os roteadores sejam percorridos
****************************************************************/
package model;

import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import java.util.Random;
import control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Path;

public class Packets extends Thread{
  private int TTL; // numero de hops / saltos que o pacote faz antes de ser descartado
  private int nodeSender; //roteador transmissor
  private int nodeReceiver; //roteador receptor
  private Polyline pathToGo; //caminho que o pacote vai percorrer na interface, em pixels
  private ImageView packet; //imagem do pacote
  private Pane Root; //referencia ao painel raiz onde o pacote sera exibido
  mainControl mC = new mainControl(); //controlador principal da aplicacao
  boolean controlFinished = true; //indica se a animacao do pacote foi finalizada
  PathTransition pathTransition = new PathTransition(); //animacao do movimento do pacote
  private boolean invertRoute = false; //controla se a rota deve ser invertida ao desenhar

  /* ******************************************************************
  * Construtor: Packets
  * Funcao: inicializa um novo objeto Packets com informacoes sobre o roteador de origem, roteador de destino, caminho, TTL, e controlador principal
  * Parametros: 
    - int sender: Identificador do roteador de origem.
    - int receiver: Identificador do roteador de destino.
    - Polyline path: Caminho que o pacote deve seguir
    - Pane root: Painel onde o pacote sera exibido
    - int ttl: Valor inicial de TTL do pacote
    - mainControl control: Controlador principal da aplicacao
  * Retorno: Construtor sem retorno
  ****************************************************************** */
  public Packets(int sender, int receiver, Polyline path, Pane root, int ttl, mainControl control) {
    nodeSender = sender;
    nodeReceiver = receiver;
    pathToGo = path;
    TTL = ttl;
    mC = control;

    Root = root; 
    packet = new ImageView(new Image("./imgs/package.png"));
    Root.getChildren().add(packet); //adiciona a imagem do pacote ao painel
    if(nodeSender > nodeReceiver){
      invertRoute = true; //inverte a rota se o roteador de origem tiver ID maior que o de destino
    }
    }
  
  /* ******************************************************************
  * Metodo: sendPacket
  * Funcao: Envia o pacote pela rota definida, controlando a animacao de movimento na interface. 
  Verifica se a rota precisa ser invertida e trata o fim da animacao.
  * Parametros: Nenhum
  * Retorno: void
  ****************************************************************** */
  public void sendPacket() {
    ObservableList<Double> points = pathToGo.getPoints(); //pega os pontos do caminho
    Path path = new Path();
  
    if(invertRoute){ //verifica se os pontos do caminho estao invertidos
      path.getElements().add(new MoveTo(points.get(2), points.get(3)));
      path.getElements().add(new LineTo(points.get(0), points.get(1)));
    } else{
      path.getElements().add(new MoveTo(points.get(0), points.get(1)));
      path.getElements().add(new LineTo(points.get(2), points.get(3)));
    }

    Platform.runLater(() -> { //executa a animacao do pacote
      pathTransition.setNode(packet);
      pathTransition.setPath(path); //define o caminho para a animacao
      pathTransition.setDuration(Duration.millis(randomTimer())); //define a duracao da animacao
      pathTransition.play();
      pathTransition.setOnFinished(event -> { //acao ao fim da animacao
      if(controlFinished){
        packet.setVisible(false); //pacote some apos animacao
        mC.getNodes().get(nodeReceiver-1).sendPackets(TTL, nodeSender); //envia o pacote para o proximo roteador
      }
    });});
  }

  /* ******************************************************************
  * Metodo: run
  * Funcao: metodo principal da thread que chama o envio do pacote
  * Parametros: Nenhum
  * Retorno: void
  ****************************************************************** */
  @Override
  public void run() {
    sendPacket();
  }

   /* ******************************************************************
  * Metodo: randomTimer
  * Funcao: gera um tempo de duracao aleatorio para a animacao do pacote dentro de um intervalo pre-definido.
  * Parametros: Nenhum
  * Retorno: int - Valor aleatorio entre 1700 e 3500 milissegundos
  ****************************************************************** */
  public int randomTimer() {
    Random random = new Random();
    int valorAleatorio = random.nextInt(1801) + 1700;
    return valorAleatorio;
  }

  /* ******************************************************************
  * Metodo: breakAnimation
  * Funcao: Interrompe a animacao em andamento e desativa o pacote na interface
  * Parametros: Nenhum
  * Retorno: void
  ****************************************************************** */
  public void breakAnimation(){
    pathTransition.stop();
    packet.setVisible(false);
    packet.setDisable(true);
  }

  //Getter and setters
  public void setControlFinished(boolean controlFinished) {
    this.controlFinished = controlFinished;
  }

  public boolean getControlFinished() {
    return this.controlFinished;
  }

  public int getNodeReceiver() {
    return nodeReceiver;
  }

  public int getNodeSender() {
    return nodeSender;
  }

  public Polyline getPathToGo() {
    return pathToGo;
  }

  public int getTTL() {
    return TTL;
  }
}
