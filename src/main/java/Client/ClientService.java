package Client;

import Service.Role;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

class ClientService {
    JSONObject clientRegister(final BufferedReader systemIn) {
        final JSONObject clientInfo = new JSONObject();
        String input;
        String[] splittedInput;

        try {
            System.out.println("Введите команду /register и свою роль с именем через пробел: ");
            input = systemIn.readLine();
            splittedInput = input.split(" ");

            while(!isInputCorrect(splittedInput)){
                System.out.println("Произошла ошибка при вводе команды. Попробуйте еще раз: ");
                input = systemIn.readLine();
                splittedInput = input.split(" ");
            }
            clientInfo.put("role", splittedInput[1]);
            clientInfo.put("name", splittedInput[2]);
        } catch (IOException e) {
            System.out.println("Произошла ошибка при вводе команды");
            e.printStackTrace();
        }

        return clientInfo;
    }

    private boolean isInputCorrect(String[] splittedInput){
        if(splittedInput.length == 3) {
            boolean rightCommand = splittedInput[0].equals("/register");
            boolean rightRole = Role.isRoleExists(splittedInput[1]);
            return rightCommand && rightRole;
        } else {
            return false;
        }
    }

    void send(String message, BufferedWriter socketOut){
        try{
            socketOut.write(message + "\n");
            socketOut.flush();
        } catch (IOException e){
            System.out.println("Произошла ошибка при отправлении сообщения. Попробуйте еще раз: ");
            e.printStackTrace();
        }
    }

    void close(Socket socket){
        if(!socket.isClosed()){
            try {
                socket.close();
                System.exit(0);
            } catch (IOException e) {
                System.out.println("Произошла ошибка при закрытии сокета");
                e.printStackTrace();
            }
        }
    }
}
