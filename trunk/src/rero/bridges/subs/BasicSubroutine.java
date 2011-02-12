/*
   SLEEP - Simple Language for Environment Extension Purposes
 .-----------------------.
 | sleep.bridges.BasicIO |____________________________________________________
 |                                                                            |
   Author: Raphael Mudge (raffi@hick.org)
           http://www.hick.org/~raffi/

   Description:
       Implementation of the subroutine concept.

   Documentation:

   Changelog:

   * This software is distributed under the artistic license, see license.txt
     for more information. *

 |____________________________________________________________________________|
 */

package rero.bridges.subs;
 
import java.util.*;
import java.io.*;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import sleep.bridges.*;

/** The actual implementation of each subroutine declared with the sub keyword.
    @see sleep.bridges.DefaultEnvironment     */
public class BasicSubroutine extends SleepClosure
{
    Stack          unload; // stack for other subroutines with the same name.

    public BasicSubroutine(ScriptInstance si, Block _code, Stack _unload)
    {
       super(si, _code);
       unload = _unload;
    }

    public Stack getUnloadStack()
    {
       return unload;
    }
}

