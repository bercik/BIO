/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.IFunction;
import pl.rcebula.module.Module;
import pl.rcebula.module.utils.error_codes.ErrorConstruct;
import pl.rcebula.module.utils.type_checker.TypeChecker;

/**
 *
 * @author robert
 */
public class TcpModule extends Module
{
    // klasa wrapper na klasę Socket
    private class MySocket
    {
        private final Socket socket;
        private final DataInputStream dis;
        private final DataOutputStream dos;

        public MySocket(Socket socket) throws IOException
        {
            this.socket = socket;
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
        }

        public void sendInt(int num) throws IOException
        {
            dos.writeInt(num);
        }

        public void sendBool(boolean bool) throws IOException
        {
            dos.writeBoolean(bool);
        }

        public void sendFloat(float num) throws IOException
        {
            dos.writeFloat(num);
        }

        public void sendString(String str) throws IOException
        {
            dos.writeUTF(str);
        }

        private int readInt() throws IOException
        {
            return dis.readInt();
        }

        private boolean readBool() throws IOException
        {
            return dis.readBoolean();
        }

        private float readFloat() throws IOException
        {
            return dis.readFloat();
        }

        private String readString() throws IOException
        {
            return dis.readUTF();
        }

        private void close() throws IOException
        {
            socket.close();
        }
    }

    // mapa, która zawiera wszystkie instancję ServerSocket. Kluczem jest port, a wartością instancja ServerSocket
    Map<Integer, ServerSocket> serverSockets = new HashMap<>();
    // mapa, która zawiera wszystkie otwarte instancję MySocket. Kluczem jest id, a wartością instancja MySocket
    Map<Integer, MySocket> sockets = new HashMap<>();

    // zmienna informująca o id socketu
    private static int connectionId = 1;

    // funkcja pomocnicza dodająca socket do mapy sockets
    private int addSocket(Socket socket) throws IOException
    {
        sockets.put(connectionId, new MySocket(socket));
        // zwróc i zinkrementuj
        return connectionId++;
    }

    @Override
    public String getName()
    {
        return "tcp";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new TcpListenFunction());
        putFunction(new TcpConnectFunction());
        putFunction(new TcpCloseFunction());
        putFunction(new TcpSendFunction());
        putFunction(new TcpRecvStringFunction());
        putFunction(new TcpRecvIntFunction());
        putFunction(new TcpRecvFloatFunction());
        putFunction(new TcpRecvBoolFunction());
    }

    @Override
    public void exit()
    {
        // zamknij wszystkie serwer sockety i sockety
        for (ServerSocket ss : serverSockets.values())
        {
            try
            {
                ss.close();
            }
            catch (IOException ex)
            {
            }
        }
        
        for (MySocket socket : sockets.values())
        {
            try
            {
                socket.close();
            }
            catch (IOException ex)
            {
            }
        }
    }

    private class TcpRecvFunction
    {
        public Data perform(List<Data> params, CallFrame currentFrame, Interpreter interpreter,
                ErrorInfo callErrorInfo, String funName, DataType dataType)
        {
            // parametr: <all>
            Data dConnId = params.get(0);

            // sprawdź czy typu int
            TypeChecker tc = new TypeChecker(dConnId, funName, 0, dConnId.getErrorInfo(), interpreter,
                    DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }

            // pobierz wartość
            int connId = (int)dConnId.getValue();
            // pobierz socket
            MySocket socket = sockets.get(connId);
            // sprawdź czy istnieje
            if (socket == null)
            {
                return ErrorConstruct.TCP_CONNECTION_DOESNT_EXIST(funName, dConnId.getErrorInfo(),
                        interpreter, connId);
            }

            try
            {
                // odbierz wartość w zależności od typu różna metoda
                switch (dataType)
                {
                    case STRING:
                        String str = socket.readString();
                        return Data.createStringData(str);
                    case INT:
                        int iNum = socket.readInt();
                        return Data.createIntData(iNum);
                    case FLOAT:
                        float fNum = socket.readFloat();
                        return Data.createFloatData(fNum);
                    case BOOL:
                        boolean bool = socket.readBool();
                        return Data.createBoolData(bool);
                    default: 
                        throw new RuntimeException("Unknown data type: " + dataType.toString());
                }
            }
            catch (IOException ex)
            {
                return ErrorConstruct.TCP_CONNECTION_ERROR(funName, callErrorInfo, interpreter);
            }
        }
    }
    
    private class TcpRecvStringFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TCP_RECV_STRING";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter, ErrorInfo callErrorInfo)
        {
            return new TcpRecvFunction().perform(params, currentFrame, interpreter, callErrorInfo, 
                    getName(), DataType.STRING);
        }
    }
    
    private class TcpRecvIntFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TCP_RECV_INT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter, ErrorInfo callErrorInfo)
        {
            return new TcpRecvFunction().perform(params, currentFrame, interpreter, callErrorInfo, 
                    getName(), DataType.INT);
        }
    }
    
    private class TcpRecvFloatFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TCP_RECV_FLOAT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter, ErrorInfo callErrorInfo)
        {
            return new TcpRecvFunction().perform(params, currentFrame, interpreter, callErrorInfo, 
                    getName(), DataType.FLOAT);
        }
    }
    
    private class TcpRecvBoolFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TCP_RECV_BOOL";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter, ErrorInfo callErrorInfo)
        {
            return new TcpRecvFunction().perform(params, currentFrame, interpreter, callErrorInfo, 
                    getName(), DataType.BOOL);
        }
    }

    private class TcpSendFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TCP_SEND";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter,
                ErrorInfo callErrorInfo)
        {
            // parametry: all, <all>+
            Data dConnId = params.get(0);

            // sprawdź czy dConnId typu int
            TypeChecker tc = new TypeChecker(dConnId, getName(), 0, dConnId.getErrorInfo(), interpreter,
                    DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }

            // sprawdź czy pozostałe parametry to: int, float, string lub bool
            List<Data> remainigParams = params.subList(1, params.size());
            tc = new TypeChecker(remainigParams, getName(), 1, interpreter, DataType.BOOL,
                    DataType.FLOAT, DataType.INT, DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }

            // pobierz wartość connId
            int connId = (int)dConnId.getValue();
            // pobierz socket
            MySocket socket = sockets.get(connId);
            // sprawdź czy takie połączenie istnieje
            if (socket == null)
            {
                return ErrorConstruct.TCP_CONNECTION_DOESNT_EXIST(getName(), dConnId.getErrorInfo(),
                        interpreter, connId);
            }
            // iteruj po wszystkich wartościach do wysłania
            for (int i = 1; i < params.size(); ++i)
            {
                Data p = params.get(i);
                try
                {
                    // w zależności od typu
                    switch (p.getDataType())
                    {
                        case STRING:
                            String str = (String)p.getValue();
                            socket.sendString(str);
                            break;
                        case INT:
                            int iNum = (int)p.getValue();
                            socket.sendInt(iNum);
                            break;
                        case FLOAT:
                            float fNum = (float)p.getValue();
                            socket.sendFloat(fNum);
                            break;
                        case BOOL:
                            boolean bool = (boolean)p.getValue();
                            socket.sendBool(bool);
                            break;
                    }
                }
                catch (IOException ex)
                {
                    return ErrorConstruct.TCP_CONNECTION_ERROR(getName(), callErrorInfo, interpreter);
                }
            }

            return Data.createNoneData();
        }
    }

    private class TcpCloseFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TCP_CLOSE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter,
                ErrorInfo callErrorInfo)
        {
            // parametr: <all>
            Data par = params.get(0);

            // sprawdź czy typu int
            TypeChecker tc = new TypeChecker(par, getName(), 0, par.getErrorInfo(), interpreter, DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }

            // pobierz wartość
            int connId = (int)par.getValue();
            // pobierz Socket pod podanym id
            MySocket socket = sockets.get(connId);
            if (socket != null)
            {
                try
                {
                    // usuń z mapy socketów
                    sockets.remove(connId);
                    // zamknij socket
                    socket.close();
                }
                catch (IOException ex)
                {
                    // do nothing
                }
            }

            return Data.createNoneData();
        }
    }

    private class TcpListenFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TCP_LISTEN";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter,
                ErrorInfo callErrorInfo)
        {
            // parametr: <all>
            Data par = params.get(0);

            // sprawdź czy typu int
            TypeChecker tc = new TypeChecker(par, getName(), 0, par.getErrorInfo(), interpreter, DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }

            // pobierz numer portu
            int port = (int)par.getValue();

            try
            {
                // sprawdź czy istnieje już w mapie serverSockets
                // jeżeli nie to dodaj nowy ServerSocket
                if (!serverSockets.containsKey(port))
                {
                    serverSockets.put(port, new ServerSocket(port));
                }

                // oczekuj na połączenie od klienta
                ServerSocket ss = serverSockets.get(port);
                Socket socket = ss.accept();

                // dodaj do mapy sockets
                Data connectionId = Data.createIntData(addSocket(socket));

                // zwróc connection id
                return connectionId;
            }
            catch (IOException ex)
            {
                return ErrorConstruct.TCP_CONNECTION_ERROR(getName(), callErrorInfo, interpreter);
            }
            catch (IllegalArgumentException ex)
            {
                return ErrorConstruct.TCP_PORT_OUT_OF_RANGE(getName(), par.getErrorInfo(), interpreter, port);
            }
        }
    }

    private class TcpConnectFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TCP_CONNECT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter,
                ErrorInfo callErrorInfo)
        {
            // parametry: <all, all>
            Data par1 = params.get(0);
            Data par2 = params.get(1);

            // sprawdź czy pierwszy parametr jest typu string
            TypeChecker tc = new TypeChecker(par1, getName(), 0, par1.getErrorInfo(), interpreter,
                    DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }

            // sprawdź czy drugi parametr jest typu int
            tc = new TypeChecker(par2, getName(), 1, par2.getErrorInfo(), interpreter, DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }

            // pobierz wartość parametrów
            String ipAddr = (String)par1.getValue();
            int port = (int)par2.getValue();

            try
            {
                Socket socket = new Socket(ipAddr, port);
                // dodaja socket i zwróc connection id
                return Data.createIntData(addSocket(socket));
            }
            catch (UnknownHostException ex)
            {
                return ErrorConstruct.TCP_BAD_IP_ADDRESS(getName(), par1.getErrorInfo(), interpreter, ipAddr);
            }
            catch (IOException ex)
            {
                return ErrorConstruct.TCP_CONNECTION_ERROR(getName(), callErrorInfo, interpreter);
            }
            catch (IllegalArgumentException ex)
            {
                return ErrorConstruct.TCP_PORT_OUT_OF_RANGE(getName(), par2.getErrorInfo(), interpreter, port);
            }
        }
    }
}
