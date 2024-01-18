package app;

import servent.handler.PullRequestHandler;
import servent.message.Message;
import servent.message.PullRequestMessage;
import servent.message.util.MessageUtil;

import java.io.*;

public class FileSystemService implements SystemService {

    private static final String FILES_PATH = "files/";

    @Override
    public void pull(String serventKey, String fileName) {

        // if key is empty try to read with our own keys

        if (serventKey.equals("")) {
            try {
                int serventId = -1;
                if (fileName.startsWith("servent")) {
                    serventId = Integer.parseInt(fileName.split("/")[0].substring(7));
                }
                boolean isFileRead = false;
                for (Integer key : AppConfig.customState.getKeys()) {
                    if (serventId == key) {
                        BufferedReader br = new BufferedReader(new FileReader(FILES_PATH + fileName));
                        String line;
                        while ((line = br.readLine()) != null) {
                            System.out.printf(line);
                        }
                        isFileRead = true;
                        break;
                    }
                }

                if (!isFileRead) {
                    Message pullRequestMessage = new PullRequestMessage(
                            AppConfig.myServentInfo.getListenerPort(),
                            0,
                            serventId + "/" + fileName.split("/")[1]
                    );
                    for (int i = 0; i < AppConfig.customState.getSuccessorTable().length; i++) {
                        pullRequestMessage = pullRequestMessage.changeReceiver(
                                AppConfig.customState.getSuccessorTable()[i].getListenerPort()
                        );

                        MessageUtil.sendMessage(pullRequestMessage.makeMeASender());
                    }
                    for (int i = 0; i < AppConfig.customState.getSuccessorTable().length; i++) {
                        pullRequestMessage = pullRequestMessage.changeReceiver(
                                AppConfig.customState.getPredecessorTable()[i].getListenerPort()
                        );
                        MessageUtil.sendMessage(pullRequestMessage.makeMeASender());
                    }
                }


            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                BufferedReader br = null;
                br = new BufferedReader(new FileReader(FILES_PATH + serventKey));
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void add(String fileName) {

        int serventId = -1;
        if (fileName.startsWith("servent")) {
            serventId = Integer.parseInt(fileName.split("/")[0].substring(7));
        }
        for (Integer key : AppConfig.customState.getKeys()) {
            if (serventId == key) {
                File file = new File(FILES_PATH + fileName);
                if (!file.exists()) {
                    try {
                        boolean isNewFileCreated = file.createNewFile();

                        if (isNewFileCreated) {
                            AppConfig.timestampedStandardPrint("Adding file: " + fileName + " was successful." );
                            break;
                        } else {
                            AppConfig.timestampedStandardPrint("Adding file: " + fileName + " has failed." );
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    AppConfig.timestampedStandardPrint("File with name : " + fileName + "already exists.");
                }
            }
        }
    }

    @Override
    public void remove(String fileName) {

        int serventId = -1;
        if (fileName.startsWith("servent")) {
            serventId = Integer.parseInt(fileName.split("/")[0].substring(7));
        }
        for (Integer key : AppConfig.customState.getKeys()) {
            if (serventId == key) {
                File file = new File(FILES_PATH  + fileName);

                if (file.exists()) {

                    try {
                        boolean isFileRemoved = file.delete();

                        if (isFileRemoved) {
                            AppConfig.timestampedStandardPrint("Removing file: " + fileName + " was successful." );
                        } else {
                            AppConfig.timestampedStandardPrint("Removing file: " + fileName + " failed." );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    AppConfig.timestampedStandardPrint("There is no file: " + fileName );
                }
            }
        }

    }
}
