package it.unisa.ascetic.refactor.splitting_algorithm.irEngine;


import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;

public class ConfigLoader {
    private static Properties config = null;

    private BaseConf base_conf = new BaseConf();

    private String file_config_path = base_conf
            .getAbsolutePathForDefaultConfigurationFile();

    /**
     * Il costruttore di questa classe di preoccupa di caricare il file di
     * configurazione se non e' mai stato caricato prima. La configurazione
     * viene caricata solo la prima volta che viene istanziata questa classe. Se
     * la configurazione del file dovesse cambiare allora si deve riavviare il
     * software per far si che le modifiche abbiano effetto.
     */
    public ConfigLoader() {
        Logger logger = Logger.getLogger("global");
        logger.severe("PATH DOVE INSERIRE IL FILE DI CONFIGURAZIONE: " + file_config_path);

        if (ConfigLoader.config == null) {
            this.loadConfig();
        }
    }

    private void loadConfig() {
        ConfigLoader.config = new Properties();
        InputStream file_conf = null;
        try {
            // [01]
            file_conf = new FileInputStream(this.file_config_path);
            // [02]
            ConfigLoader.config.load(file_conf);

        } catch (FileNotFoundException e) {
            // Errore nell'apertura dello stream di input [01]
            e.printStackTrace();
        } catch (IOException e) {
            // Errore nel caricamento del file di configurazione [02]

            e.printStackTrace();
        } finally {
            try {
                file_conf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void storeConfig() {
        try {
            OutputStream file_conf = new FileOutputStream(this.file_config_path);
            ConfigLoader.config.store(file_conf, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Questo metodo permette di ottenere un valore di una proprieta' del file
     * di configurazione.
     *
     * @param key Chiave della proprieta'
     * @return Valore della proprieta'
     */
    public String getProperties(String key) {
        return ConfigLoader.config.getProperty(key);
    }

    public void setProperties(String key, String value) {
        ConfigLoader.config.setProperty(key, value);
    }
}
