package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientSender implements Runnable {
    private String userInput;
    private BufferedReader systemIn;
    private ClientService clientService;
    private Socket socket;
    private BufferedWriter socketOut;

    ClientSender(final Socket socket, final BufferedWriter socketOut, final ClientService clientService) {
        this.socket = socket;
        this.socketOut = socketOut;
        this.clientService = clientService;
    }

    @Override
    public void run() {
        systemIn = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            userInput = null;
            try{
                while(true){
                    userInput = systemIn.readLine().trim();
                    if(!userInput.equals("")){
                        break;
                    }
                    System.out.println("Сообщение не может быть пустым!");
                }
            } catch (IOException e){
                System.out.println("Произошла ошибка при вводе сообщения");
                e.printStackTrace();
            }
            if (userInput == null || socket.isClosed()){
                System.out.println("Произошла ошибка при отправке информации на сервер");
                clientService.close(socket);
                break;
            } else {
                clientService.send(userInput, socketOut);
            }
        }
    }
}
