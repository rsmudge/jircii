package rero.client.script;

import rero.client.DataStructures;
import rero.client.Feature;
import rero.config.ClientDefaults;
import rero.config.ClientState;
import rero.config.ClientStateListener;
import rero.config.StringList;
import rero.util.ClientUtils;
import sleep.error.RuntimeWarningWatcher;
import sleep.error.ScriptWarning;
import sleep.error.SyntaxError;
import sleep.error.YourCodeSucksException;
import sleep.runtime.ScriptInstance;
import sleep.runtime.ScriptLoader;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.Charset;

public class ScriptManager extends Feature implements ClientStateListener, RuntimeWarningWatcher
{
    protected ScriptLoader loader;
    protected Hashtable environment; // shared environment...
    protected boolean lock = false;

    private static boolean SILENT = false;  // for /reload mainly...

    public void init()
    {
        loader = (ScriptLoader) getCapabilities().getDataStructure(DataStructures.ScriptLoader);
        environment = (Hashtable) getCapabilities().getDataStructure(DataStructures.SharedEnv);

        ClientState.getClientState().addClientStateListener("script.files", this);
    }

    public void storeDataStructures(WeakHashMap data)
    {
        data.put(DataStructures.ScriptManager, this);
    }

    public void hashScripts()
    {
	// TODO: When we load, traverse all the loaded scripts and, if the path is not absolute, change it to absolute and save it. We must enforce absolute paths!

        if (lock)
            return;

        Set unload, load, configured;

	// Traverse through the loaded scripts and correct the script paths to absolute paths, as that's the format in which we've stored them.
        configured = new LinkedHashSet();
	Iterator si = ClientState.getClientState().getStringList("script.files").getList().iterator();

	while (si.hasNext()) {
		File absScript = new File(si.next().toString());
		configured.add(absScript.getAbsolutePath());
	}

        //configured.addAll(ClientState.getClientState().getStringList("script.files").getList());

        load = loader.getScriptsToLoad(configured);
        unload = loader.getScriptsToUnload(configured);

	// Exclude the resource scripts
        load.remove("menus");
        load.remove("default");
        load.remove("lame");

        unload.remove("menus");
        unload.remove("lame");
        unload.remove("default");

        Iterator i = unload.iterator();
        while (i.hasNext()) {
            String tt = (String) i.next();
            loader.unloadScript(tt);

            if (ClientState.getClientState().isOption("script.verboseLoad", ClientDefaults.script_verboseLoad)) {
                getCapabilities().getUserInterface().printStatus("Successfully unloaded script " + tt);
            }
        }

        i = load.iterator();
        while (i.hasNext()) {
            String tt = (String) i.next();
            if (!loader.isLoaded(tt)) {
                internalScriptLoad(tt); // this check is overly redundant, however if a script loads a script while this operation is
                // taking place things will have the potential to break, never any fun
            }
        }
    }

    public void loadTheme(String filename)
    {
        filename = ClientUtils.getFile(filename).getAbsolutePath();

        if ((new File(filename)).exists()) {
            internalScriptLoad(filename);
            loader.unloadScript(filename);
        } else {
            getCapabilities().getUserInterface().printStatus("Error loading theme: " + new File(filename).getAbsolutePath() + " does not exist");
        }
    }

    // Checks whether a script is in the script.files property list. It determines via getAbsolutePath() on each iteration of script.files, and of the passed argument filename.
    public boolean isInScriptList(String filename)
    {
	    File fn = ClientUtils.getFile(filename);
	    Iterator si = ClientState.getClientState().getStringList("script.files").getList().iterator();

	    while (si.hasNext())
	    {
		    File siFn = new File(si.next().toString());
		    if (siFn.getAbsolutePath().equals(fn.getAbsolutePath())) {
			    return true;
		    }
	    }

	    return false;
    }

    public void addScript(String filename)
    {
        StringList temp = ClientState.getClientState().getStringList("script.files");

	// Sanity checks
	File fn = ClientUtils.getFile(filename);

        if (fn.exists()) {
	    if (isInScriptList(fn.getAbsolutePath())) {
	    	// Already present in the script list script.files; so we're already loaded.
		getCapabilities().getUserInterface().printStatus("Script file " + fn.getName() + " is already loaded.  Grabbing a beer instead :D");
		return;
	    }

	    // Must not be present -- we made it this far.
            temp.add(fn.getAbsolutePath());
            temp.save();
            ClientState.getClientState().sync();	// This triggers a rehash
        } else {
            getCapabilities().getUserInterface().printStatus("Error loading script: " + fn.getName() + " (" + fn.getAbsolutePath() + ") does not exist!");
        }
    }

    public void evalScript(String code)
    {
        try {
            getCapabilities().getUserInterface().printActive(((ScriptInstance) loader.getScripts().getFirst()).getScriptEnvironment().evaluateExpression(code).toString());
        }
        catch (YourCodeSucksException ex) {
            formatCodeException("/eval input", ex);
        }
    }

    public boolean setDebug(String filename, String debuglevel)
    {
	String dFile = findScript(filename);

	if (dFile != null) {
   	     setDebugReal(dFile, debuglevel);
	     return true;
	} else {
	     getCapabilities().getUserInterface().printStatus("Could not find loaded script " + filename + " to set debug level.");
	     return false;
	}
    }

    private void setDebugReal(String filename, String debuglevel)
    {
        ScriptInstance script = (ScriptInstance)loader.getScriptsByKey().get(filename);

        if (script == null)
        {
           //getCapabilities().getUserInterface().printStatus("Could not find loaded script " + filename + " to set debug level.");
           return;
        }

        int debug = script.getDebugFlags();

        if (debuglevel.indexOf("+trace") > -1)
        {
           debug = debug | ScriptInstance.DEBUG_TRACE_CALLS; 
        }
     
        if (debuglevel.indexOf("-trace") > -1)
        {
           debug = debug & ~ScriptInstance.DEBUG_TRACE_CALLS; 
        }

        if (debuglevel.indexOf("-warn") > -1)
        {
           debug = debug & ~ScriptInstance.DEBUG_SHOW_WARNINGS; 
        }

        if (debuglevel.indexOf("+warn") > -1)
        {
           debug = debug | ScriptInstance.DEBUG_SHOW_WARNINGS; 
        }

        if (debuglevel.indexOf("+all") > -1)
        {
           debug = debug | ScriptInstance.DEBUG_SHOW_WARNINGS | ScriptInstance.DEBUG_TRACE_CALLS;
        }

        if (debuglevel.indexOf("-all") > -1)
        {
           debug = debug & ~ScriptInstance.DEBUG_SHOW_WARNINGS & ~ScriptInstance.DEBUG_TRACE_CALLS;
        }

        script.setDebugFlags(debug);

        getCapabilities().getUserInterface().printStatus("*** Updated debug flags for script " + filename);
    }

    public String evalString(String code)
    {
        try {
            return ((ScriptInstance) loader.getScripts().getFirst()).getScriptEnvironment().evaluateExpression(code).toString();
        }
        catch (YourCodeSucksException ex) {
            formatCodeException("/eval input", ex);
        }
        return code;
    }

    public void reloadScript(String filename)
    {
        boolean flag = true;
	String rScript = findScript(filename);

	if (rScript != null) {
            removeScript(rScript);
            addScript(rScript);
        }
	else {
            getCapabilities().getUserInterface().printStatus("Error re-loading script: " + filename + " isn't loaded! Try /load'ing it first.");
	}
    }

    // Returns the absolute path of a script file found in script.files matching argument filename. Replaces findScripts().
    private String findScript(String filename)
    {
	    File fn = new File(filename);

	    if (isInScriptList(fn.getAbsolutePath())) {
		    return fn.getAbsolutePath();
	    }

  	    return null;
    }

    public boolean removeScript(String filename)
    {
        StringList temp = ClientState.getClientState().getStringList("script.files");
	String remMe = findScript(filename);

	if (remMe != null) {
        	temp.remove(remMe);
      	        temp.save();
		ClientState.getClientState().sync(); // this will cause all the script values to be rehashed...

		return true;
	}
	else
		return false;
    }

    private static boolean lame = true;

    public void loadLameScripts()
    {
        try {
            if (lame) {
                ScriptInstance defaults =
                    loader.loadScript("lame", ClientState.getClientState().getResourceAsStream("lame.irc"), environment);

                defaults.addWarningWatcher(this);
                defaults.runScript();

                lame = false;
            }
        }
        catch (Exception ex) {

        }
    }

    public void cleanup()
    {
        Iterator i = loader.getScripts().iterator();
        while (i.hasNext())
        {
           ScriptInstance script = (ScriptInstance)i.next();
           script.setUnloaded(); /* tell the script its dead */
        }
    }

    public void loadScripts()
    {
        //
        // do other fun stuff... i.e. script loading and such
        //

	    // TODO: Deal with all of this stuff.
        try {
            if (ClientState.getClientState().isOption("load.default", true)) {
                long start = System.currentTimeMillis();
                ScriptInstance defaults =
                    loader.loadScript("default", ClientState.getClientState().getResourceAsStream("default.irc"), environment);
//            System.out.println("Default script loaded in: " + (System.currentTimeMillis() - start));

                defaults.addWarningWatcher(this);
                defaults.runScript();
            }

            if (ClientState.getClientState().isOption("load.menus", true)) {
                long start = System.currentTimeMillis();
                ScriptInstance defaults =
                    loader.loadScript("menus", ClientState.getClientState().getResourceAsStream("menus.irc"), environment);
//            System.out.println("Menu script loaded in: " + (System.currentTimeMillis() - start));

                defaults.addWarningWatcher(this);
                defaults.runScript();
            }

            if (ClientState.getClientState().isOption("load.lame", false)) {
                loadLameScripts();
            }
        }
        catch (YourCodeSucksException ex) {
            formatCodeException("<Internal Scripts>", ex);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        Iterator i = ClientState.getClientState().getStringList("script.files").getList().iterator();
        while (i.hasNext()) {
            internalScriptLoad((String) i.next());
        }
    }

    public void propertyChanged(String name, String value)
    {
        hashScripts();
    }

    private boolean internalScriptLoad(String scriptFile)
    {
	File fScript = new File(scriptFile);

	if (!fScript.exists())
	{
		// Script doesn't exist
		//System.err.println("internalScriptLoad(): Cannot find file " + scriptFile);
		return false;
	} else if (!fScript.canRead() || !fScript.isFile()) {
		//System.err.println("internalScriptLoad(): Cannot load script, check permissions and file type for script " + scriptFile);
		return false;
	}

	if (loader.isLoaded(fScript.getAbsolutePath()))
	{
		// We shouldn't get here unless something funky is happening with the script.files property... (?)
		getCapabilities().getUserInterface().printStatus("Error loading script " +  fScript.getName() + ": already loaded. [internalScriptLoad()]");
		return false;
	}

        try {
            String charset = guessCharsetFromFileName(scriptFile);

            // keep old conversion mode
            boolean charsetConversions = loader.isCharsetConversions();
            String oldcharset = loader.getCharset();

            // set charset to use when loading script
            if (charset != null) {
                loader.setCharsetConversion(true);
                loader.setCharset(charset);
            }

            ScriptInstance scripti = null;
            InputStream ii = ClientState.getClientState().getResourceAsStream(scriptFile);

            if (ii != null) 
            {
                scripti = loader.loadScript(fScript.getAbsolutePath(), ii, environment);
            }
	    else
	    {
		    // We couldn't open the file ... bail out.
		    if (charset != null) {
			    loader.setCharsetConversion(charsetConversions);
			    loader.setCharset(oldcharset);
		    }
	
		    return false;
	    }
/*            else 
            {
                loader.loadScript(scriptFile, environment);
            }
*/
            // restore previous mode
            if (charset != null) {
                loader.setCharsetConversion(charsetConversions);
                loader.setCharset(oldcharset);
            }

            scripti.addWarningWatcher(this);

            if (ClientState.getClientState().isOption("script.verboseLoad", ClientDefaults.script_verboseLoad)) {
                getCapabilities().getUserInterface().printStatus("Successfully loaded script " + new File(scriptFile).getName());
            }

            scripti.runScript();
	    ii.close(); // Close the InputStream
        }
        catch (YourCodeSucksException ex) {
            formatCodeException(scriptFile, ex);

            lock = true;
            removeScript(scriptFile);
            lock = false;

	    return false;
        }
        catch (IOException ex2) {
            getCapabilities().getUserInterface().printStatus("Error loading " + (new File(scriptFile)).getName() + ": " + ex2.getMessage());
	    return false;
        }
        catch (Exception ex3) {
            getCapabilities().getUserInterface().printStatus("Error loading " + (new File(scriptFile)).getName() + ": " + ex3.getMessage() + " <-- could be a sleep bug, please report :)");
            ex3.printStackTrace();
	    return false;
        }

	return true;
    }

    private static final Pattern ENCODING_IN_FILE = Pattern.compile(".*\\.(.*?)\\..*$");
    private static final Pattern ENCODING_IN_EXTENSION = Pattern.compile(".*\\.(.*)$");
    private static SortedMap charsets = null;

    /**
     * Script file name may contain charset, like file.charset.irc, or just file.charset
     *
     * @param scriptFile file name
     * @return valid charset if found in file name, null otherwise
     */
    private String guessCharsetFromFileName(String scriptFile)
    {
        File f = new File(scriptFile);
        String name = f.getName();

        Matcher mname = ENCODING_IN_FILE.matcher(name);
        Matcher mext = ENCODING_IN_EXTENSION.matcher(name);

        if (mname.matches()) {
            // lazy init
            if (charsets == null) charsets = Charset.availableCharsets();
            String charset = mname.group(1);
            if (charsets.containsKey(charset))
                return charset;
        }
        if (mext.matches()) {
            String charset = mext.group(1);
            // skip for .irc files
            if(charset.equalsIgnoreCase("irc")) return null;
            // lazy init
            if (charsets == null) charsets = Charset.availableCharsets();
            if (charsets.containsKey(charset))
                return charset;
        }

        return null;
    }

    public void processScriptWarning(ScriptWarning warn)
    {
        if (! ClientState.getClientState().isOption("script.ignoreWarnings", ClientDefaults.script_ignoreWarnings)) {
            String[] temp = warn.getMessage().split("\n");


            String fname = warn.getNameShort();

            if (warn.isDebugTrace())
            {
               getCapabilities().getUserInterface().printStatus(fname + ":" + warn.getLineNumber() + " " + temp[0]);
            }
            else
            {
               getCapabilities().getUserInterface().printStatus("*** Script Warning: " + temp[0] + " at " + fname + ":" + warn.getLineNumber());
            }

            for (int x = 1; x < temp.length; x++) {
                getCapabilities().getUserInterface().printStatus("     " + temp[x]);
            }
        }
    }

    private void formatCodeException(String scriptFile, YourCodeSucksException ex)
    {
        getCapabilities().getUserInterface().printStatus("*** " + ex.getErrors().size() + " error(s) loading " + scriptFile);

        Iterator i = ex.getErrors().iterator();
        while (i.hasNext()) {
            SyntaxError anError = (SyntaxError) i.next();
            getCapabilities().getUserInterface().printStatus("Error: " + anError.getDescription() + " at line " + anError.getLineNumber());
            getCapabilities().getUserInterface().printStatus("       " + anError.getCodeSnippet());

            if (anError.getMarker() != null)
                getCapabilities().getUserInterface().printStatus("       " + anError.getMarker());
        }
    }
}

