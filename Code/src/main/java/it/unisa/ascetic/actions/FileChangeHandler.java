package it.unisa.ascetic.actions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.messages.MessageBusConnection;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileChangeHandler implements ApplicationComponent, BulkFileListener {

    private final MessageBusConnection connection;
    private List<String> updateList;
    private File modificationFile;
    private PrintWriter writer;
    private Logger logger = Logger.getLogger("global");

    public FileChangeHandler() {
        connection = ApplicationManager.getApplication().getMessageBus().connect();
        updateList = new ArrayList<>();
        modificationFile = new File(System.getProperty("user.home") + "/.ascetic/changes.csv");
        logger.setLevel(Level.OFF);
    }

    public void initComponent() {
        connection.subscribe(VirtualFileManager.VFS_CHANGES, this);
        if (!modificationFile.exists()) {
            try {
                if ((new File(System.getProperty("user.home") + File.separator + ".ascetic")).exists()) {
                    modificationFile.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void disposeComponent() {
        connection.disconnect();
        modificationFile.delete();
    }

    public void before(List<? extends VFileEvent> events) {
        for (VFileEvent event : events) {
            if (isAJavaFile(event.toString())) {
                if (isCreateOperation(getOperation(event))) {
                    writeCreateToFileIfNotExists(event.toString());

                } else if (isUpdateOperation(getOperation(event))) {
                    writeUpdateToFileIfNotExists(event.toString());

                } else if (isDeleteOperation(getOperation(event))) {
                    writeDeleteToFileIfNotExists(event.toString());
                }
            }


        }

    }


    public void after(List<? extends VFileEvent> events) {
    }

    private void writeOperationToFile(String filename, String operation) {
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(modificationFile, true)));
            writer.append(filename + "," + operation + "\n");
            writer.close();
            logger.info("Written " + filename + "," + operation + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDeleteToFileIfNotExists(String eventString) {
        Pattern pathPattern = Pattern.compile("[\\/\\\\]src[\\/\\\\].+]");
        Matcher pathMatcher = pathPattern.matcher(eventString);
        if (pathMatcher.find()) {
            String path = pathMatcher.group(0);
            //System.out.println(path);
            String fullQualifiedName = path.substring(5, path.indexOf(".java"))
                    .replace('/', '.')
                    .replace("]", "");

            writeOperationToFile(fullQualifiedName, "delete");

        }

    }


    private void writeUpdateToFileIfNotExists(String eventString) {
        Pattern pathPattern = Pattern.compile("[\\/\\\\]src[\\/\\\\].+]");
        Matcher pathMatcher = pathPattern.matcher(eventString);
        if (pathMatcher.find()) {
            String path = pathMatcher.group(0);
            //System.out.println(path);
            String fullQualifiedName = path.substring(5, path.indexOf(".java"))
                    .replace('/', '.')
                    .replace("]", "");
            if (!updateList.contains(fullQualifiedName)) {
                writeOperationToFile(fullQualifiedName, "update");
                updateList.add(fullQualifiedName);
            }
        }


    }

    private void writeCreateToFileIfNotExists(String eventString) {
        //System.out.println(eventString);
        StringBuilder fullQualifiedName = new StringBuilder();

        Pattern pathPattern = Pattern.compile("[\\/\\\\]src[\\/\\\\].+]");
        Matcher pathMatcher = pathPattern.matcher(eventString);
        if (pathMatcher.find()) {
            String path = pathMatcher.group(0);
            //System.out.println(path.replace('/','.'));
            fullQualifiedName.append(path.substring(5)
                    .replace('/', '.')
                    .replace("]", ""));
        }

        Pattern regex = Pattern.compile("file (.*?)(\\.java)");
        Matcher matcher = regex.matcher(eventString);
        if (matcher.find()) {
            String name = matcher.group(0);
            fullQualifiedName.append('.').append(name.substring(name.lastIndexOf(" ") + 1, name.indexOf(".java")));
        }

        writeOperationToFile(fullQualifiedName.toString(), "create");
    }

    private boolean isCreateOperation(String operation) {
        if (operation != null) {
            return operation.equalsIgnoreCase("create");
        }
        return false;
    }

    private boolean isUpdateOperation(String operation) {
        if (operation != null) {
            return operation.equalsIgnoreCase("update");
        }
        return false;
    }

    private boolean isDeleteOperation(String operation) {
        if (operation != null) {
            return operation.equalsIgnoreCase("delete");
        }
        return false;
    }

    private boolean isAJavaFile(String eventString) {
        logger.info("Is a java file");
        return eventString.contains(".java");
    }

    private String getOperation(VFileEvent e) {
        String eventString = e.toString();
        if (eventString.contains("update")) {
            return "update";
        } else if (eventString.contains("create")) {
            return "create";
        } else if (eventString.contains("delete")) {
            return "delete";
        } else {
            return null;
        }
    }

}
