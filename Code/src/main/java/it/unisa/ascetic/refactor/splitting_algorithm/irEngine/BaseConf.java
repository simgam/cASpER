package it.unisa.ascetic.refactor.splitting_algorithm.irEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class BaseConf {
    // nome dell'applicazione
    private String app_name = "ARIEL";

    // nome del file di configurazione di default
    private String default_configuration_filename = "default.conf";

    private String default_error_filename = "default.error";

    // Cartella in cui si deve andare a creare la configurazione
    // dell'applicazione
    private String default_home_dir = "";

    private String default_configuration = this.defaultConfigurationContent();

    /**
     * Costruttore della classe BaseConf, se necessario crea la configurazione
     * standard
     */
    public BaseConf() {
        this.default_home_dir = System.getProperty("user.home") + "/." + this.app_name
                + "/";
        // controllo se esite la cartella di home dell'applicazione
        File home_dir = new File(this.default_home_dir);
        if (home_dir.exists() == false) {
            // la cartella non esiste deve essere creata
            home_dir.mkdir();
        }

        // controllo se esite il file di configurazione standard
        // dell'applicazione
        File configuration_file = new File(this
                .getAbsolutePathForDefaultConfigurationFile());
        if (configuration_file.exists() == false) {
            // il file non esiste deve essere creato
            this.createStandardConfig(configuration_file);
        }

    }

    /**
     * Questo metodo permette di scrivere la configurazione standard
     * dell'applicazione su un file.
     *
     * @param configuration_file file in cui deve essere scritta la configurazione standard.
     */
    private void createStandardConfig(File configuration_file) {
        try {
            PrintStream configuration = new PrintStream(configuration_file);
            configuration.println(this.default_configuration);
            configuration.close();
        } catch (FileNotFoundException e) {

        }
    }

    /**
     * Ritorna il path assoluto comprensivo di file name per il file di
     * configurazione standard
     *
     * @return
     */
    public String getAbsolutePathForDefaultConfigurationFile() {
        return default_home_dir + "/" + this.default_configuration_filename;
    }

    public String getAbsolutePathForDefaultErrorFile() {
        return default_home_dir + "/" + this.default_error_filename;
    }

    /**
     * Ritorna il nome dell'applicazione
     *
     * @return App name
     */
    public String getAppName() {
        return app_name;
    }

    public String getDefaultErrorFilename() {
        return default_error_filename;
    }

    /**
     * Ritorna il nome del file di configurazione di default dell'applicazione
     *
     * @return
     */
    public String getDefaultConfigurationFilename() {
        return default_configuration_filename;
    }

    /**
     * Ritorna la cartella in cui e' salvata la configurazione dell'applicazione
     * e i suoi vari file di supporto
     *
     * @return
     */
    public String getDefaultHomeDir() {
        return default_home_dir;
    }

    private String defaultConfigurationContent() {
        String defaultConfiguration = "BadChars = \\!,\u00C2,\u00A3,$,%,&,/,(,),\\=,?,',\",*,+,\\,-,;,{,},[,],_";
        defaultConfiguration += "\nBadWords=public,private,protected,void,int,float,double,boolean,string,integer,if,else,while,for,try,throws,catch,switch,class,a,about,above,after,again,against,all,am,an,and,any,are,aren't,as,at,be,because,been,before,being,below,between,both,but,by,can't,cannot,could,couldn't,did,didn't,do,does,doesn't,doing,don't,down,during,each,few,for,from,further,had,hadn't,has,hasn't,have,haven't,having,he,he'd,he'll,he's,her,here,here's,hers,herself,him,himself,his,how,how's,i,i'd,i'll,i'm,i've,if,in,into,is,isn't,it,it's,its,itself,let's,me,more,most,mustn't,my,myself,no,nor,not,of,off,on,once,only,or,other,ought,our,ours,ourselves,out,over,own,same,shan't,she,she'd,she'll,she's,should,shouldn't,so,some,such,than,that,that's,the,their,theirs,them,themselves,then,there,there's,these,they,they'd,they'll,they're,they've,this,those,through,to,too,under,until,up,very,was,wasn't,we,we'd,we'll,we're,we've,were,weren't,what,what's,when,when's,where,where's,which,while,who,who's,whom,why,why's,with,won't,would,wouldn't,you,you'd,you'll,you're,you've,your,yours,yourself,yourselves";

        return defaultConfiguration;
    }

}
