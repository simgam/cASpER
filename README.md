## How to install manually

To be able to install cASpER it is essential to have on your device:

  1. IntelliJ IDEA (starting from version 2019.1.3)
    
  2. the .zip file of the plug-in, downloadable from GitHub from the following link: https://github.com/simgam/cASpER/tree/master/Code/build/distributions/casper-1.0-SNAPSHOT.zip

Once the preparations are complete, open the Settings console (via the "File" menu or Ctlr + Alt + s shortcut) and select the "Plugins" sub-item.

Select, as shown in Figure figure: Settings Screen, the "Install Plugin from Disk" item and search for the .zip file of the plug-in. Click OK to apply the changes and restart the IDE, if required, to complete the ASCETIC installation. If the process has been successful, returning to the control window, the tool among the installed plug-ins will be visible in the "Plugins" menu.

Once the installation is complete, the cASpER- Analyze Project section will appear in the drop-down menu of the tools, providing the user with the possibility to access the plug-in functions using the appropriate items.

## How to install via Jetbrains store

cASpER can be installed through the official store. As a first step you need to open the settings panel and access the plugins store. Then select and search for this plugin under "cASpER".

## How to see the source code

To view the open source code, download the .zip file from this repository. Open the project by accessing the "Code" subfolder and selecting "java" as the source foulder.
If not present, it is also necessary to define the startup configuration to compile the plugin. Then create a gradle run configuration and enter the following values in the appropriate fields:

  1. :runIde in the "task" field
  2. -stacktrace in the "arguments" field
