#include <windows.h>
#include <windowsx.h>
#include <commctrl.h>
#include <string.h>
#include <winreg.h>
#include <stdio.h>
#include <stdlib.h>
#include <winerror.h>
#include <direct.h>
#include <shellapi.h>
#include "jirciires.h"

char errorMessage[2048 * 2];
char value[2048];
char tempStr[2048];
char newKey[2048];
char cmdLine[2048 * 2];

void DisplayError(char * message, int code)
{
     LPVOID lpMsgBuf;

     if (code > 0)
        FormatMessage(FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM, NULL, code, MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), (LPTSTR) &lpMsgBuf, 0, NULL);

     strcpy(errorMessage, message);

     if (code > 0)
     {
        strcat(errorMessage, ": ");
        strcat(errorMessage, lpMsgBuf);
        strcat(errorMessage, "jIRCii will not launch.");
     }

     // Display the string.
     MessageBox(NULL, errorMessage, "jIRCii Error", MB_OK | MB_ICONWARNING);

     // Free the buffer.
     LocalFree( lpMsgBuf );
}

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, INT nCmdShow)
{
     HKEY  regKey;
     DWORD size  = 2048;
     int   result;

     //
     // Obtain the current Java version and build a registry key from it
     //
     if ((result = RegOpenKeyEx(HKEY_LOCAL_MACHINE, "Software\\JavaSoft\\Java Runtime Environment", 0, KEY_READ, &regKey)) != ERROR_SUCCESS)
     {
        DisplayError("Couldn't find installed Java", result);
        return 0;
     }

     if ((result = RegQueryValueEx(regKey, "CurrentVersion", NULL, NULL, value, &size)) != ERROR_SUCCESS)
     {
        DisplayError("Couldn't determine Java version", result);
        return 0;
     }

     value[size] = '\0';

     size = 2048; // we have to reset the size of our buffer or the reg query will fail...

     // build our "new key" string...
     strcpy(newKey, "Software\\JavaSoft\\Java Runtime Environment\\");
     strcat(newKey, value);

     //
     // Use the built registry key to figure out where the latest Java is located at
     // 
     if ((result = RegOpenKeyEx(HKEY_LOCAL_MACHINE, newKey, 0, KEY_READ, &regKey)) != ERROR_SUCCESS)
     {
        DisplayError("Couldn't query installed Java", result);
	return 0;
     }

     if ((result = RegQueryValueEx(regKey, "JavaHome", NULL, NULL, value, &size)) != ERROR_SUCCESS)
     {
        DisplayError("Couldn't locate Java installation", result);
        return 0;
     }

     value[size] = '\0';
     strcat(value, "\\bin\\javaw.exe");

     _getcwd(newKey, 2048);
     strcpy(tempStr, newKey);
     strcat(tempStr, "\\jerk.jar");

     // make sure the jerk.jar file exists...
     if (fopen(tempStr, "r") == NULL)
     {
        strcpy(value, "Couldn't find jerk.jar at location:\n");
        strcat(value, tempStr);
        DisplayError(value, 0);
        return 0;
     }

     // build the command line options
     strcpy(cmdLine, "-jar \"");
     strcat(cmdLine, tempStr);
     strcat(cmdLine, "\" ");
     strcat(cmdLine, lpCmdLine);

     // launch [PATH TAKEN FROM REGISTRY]\bin\javaw.exe -jar [CURRENT DIRECTORY]\jerk.jar [USER SPECIFIED COMMAND LINE ARGUMENTS]
     if (ShellExecute(NULL, NULL, value, cmdLine, newKey, SW_SHOWNORMAL) <= (HINSTANCE)32)
     {
        DisplayError("Unable to launch jIRCii", 0);
     }

     return 0;
}

