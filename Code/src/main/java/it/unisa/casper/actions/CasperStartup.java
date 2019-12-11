package it.unisa.casper.actions;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class CasperStartup {

    @NotNull
    public String getComponentName() {
        return "cASpER";
    }

    public void initComponent() {
        StringBuilder dir = new StringBuilder();
        dir.append(System.getProperty("user.home"));
        dir.append(File.separator);
        dir.append(".casper");

        // Lists all files in folder
        File folder = new File(dir.toString());
        File fList[] = folder.listFiles();
        // Searchs .db
        if (fList != null) {
            for (int i = 0; i < fList.length; i++) {
                File pes = fList[i];
                if (pes.getName().endsWith(".db")) {
                    // and deletes
                    boolean success = fList[i].delete();
                }
            }
        }
    }
}
