package app;

import servent.message.WelcomeMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
public class CustomState {

    private final static int CUSTOM_LEVEL = 2;
    private final static int PORT_DIVISOR = 100;
    private final static int PORT_SUBTRAHEND = 1100;
    private ServentInfo[] successorTable;
    private ServentInfo[] predecessorTable;

    private ServentInfo[] ssTable;
    private ServentInfo[] spTable;
    private List<ServentInfo> allNodeInfo;
    Timer timer;
    private List<Integer> keys;

    FileSystemService fileSystemService;
    public CustomState() {
        successorTable = new ServentInfo[CUSTOM_LEVEL];
        predecessorTable = new ServentInfo[CUSTOM_LEVEL];
        ssTable = new ServentInfo[CUSTOM_LEVEL];
        spTable = new ServentInfo[CUSTOM_LEVEL];

        allNodeInfo = new ArrayList<>();

        timer = new Timer();
        fileSystemService = new FileSystemService();
        keys = new ArrayList<>();
        for (int i = 0; i < CUSTOM_LEVEL; i++) {

            // my successor/predecessor table
            successorTable[i] = null;
            predecessorTable[i] = null;

            // my successor's successor/predecessor table
            ssTable[i] = null;
            spTable[i] = null;
        }


    }

    public void init(WelcomeMessage welcomeMessage) {
        //tell bootstrap this node is not a collider
        try {
            Socket bsSocket = new Socket("localhost", AppConfig.BOOTSTRAP_PORT);

            PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
            bsWriter.write("New\n" + AppConfig.myServentInfo.getListenerPort() + "\n");

            bsWriter.flush();
            bsSocket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int customHash(int value) {
        return (value - PORT_SUBTRAHEND) / PORT_DIVISOR;
    }

    public boolean isCollision(int chordId) {
        if (chordId == AppConfig.myServentInfo.getCustomId()) {
            return true;
        }
        for (ServentInfo serventInfo : allNodeInfo) {
            if (serventInfo.getListenerPort() == chordId) {
                return true;
            }
        }
        return false;
    }

    public void addNodes(List<ServentInfo> newNodes) {
        allNodeInfo.clear();
        allNodeInfo.addAll(newNodes);
    }

    public void addKeys(List<Integer> newKeys) {
        keys.clear();
        keys.addAll(newKeys);
    }

    public List<ServentInfo> getAllNodeInfo() {
        return allNodeInfo;
    }

    public ServentInfo[] getSuccessorTable() {
        return successorTable;
    }

    public ServentInfo[] getPredecessorTable() {
        return predecessorTable;
    }

    public ServentInfo[] getSsTable() {
        return ssTable;
    }

    public ServentInfo[] getSpTable() {
        return spTable;
    }
    public void setSsTable(ServentInfo[] ssTable) {
        this.ssTable = ssTable;
    }

    public void setSpTable(ServentInfo[] spTable) {
        this.spTable = spTable;
    }


    public Timer getTimer() {
        return timer;
    }

    public FileSystemService getFileSystemService() {
        return fileSystemService;
    }

    public List<Integer> getKeys() {
        return keys;
    }



}
