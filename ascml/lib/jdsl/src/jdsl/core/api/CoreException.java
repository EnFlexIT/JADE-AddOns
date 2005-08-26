/*
  Copyright (c) 1999, 2000 Brown University, Providence, RI and
  Algomagic Technologies, Inc., Belmont, MA
  
                            All Rights Reserved
  
  Permission to use, copy, modify, and distribute this software and its
  documentation for any purpose other than its incorporation into a
  commercial product is hereby granted without fee, provided that the
  above copyright notice appear in all copies and that both that
  copyright notice and this permission notice appear in supporting
  documentation, and that the names of Brown University and Algomagic
  Technologies, Inc., not be used in advertising or publicity pertaining
  to distribution of the software without specific, written prior
  permission.
  
  BROWN UNIVERSITY AND ALGOMAGIC TECHNOLOGIES, INC., DISCLAIM ALL
  WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING ALL IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
  IN NO EVENT SHALL BROWN UNIVERSITY AND ALGOMAGIC TECHNOLOGIES, INC.,
  BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY
  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS,
  WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION,
  ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS
  SOFTWARE.
*/

package jdsl.core.api;

/** 
 * This is the class from which all exceptions of the core package are
 * descended.<p>
 * 
 * Future plan:  to have a stack of messages, rather than a single message.
 * That way, an exception thrown at a low level can have a low-level
 * message associated with it, but if a higher-level function intercepts
 * it, the higher-level function can attach a more meaningful message.
 * But it may turn out that the low-level message was more helpful; a
 * stack would preserve both messages.<p>
 * 
 * If you want the compiler to help you check special cases that you might
 * have missed, copy all the exceptions to your own directory, change
 * "extends RuntimeException" to "extends Exception," modify your
 * CLASSPATH to look first at your own directory, later here.
 * When you compile again, the compiler will give you a huge number of
 * "Exception must be caught or declared as thrown" errors, a small
 * fraction of which will point to special cases you have overlooked.<p>
 * 
 * @author Mark Handy
 * @version JDSL 2.1 
 */

public abstract class CoreException extends java.lang.RuntimeException {
    public CoreException (String message) {
	super (message);
    }
    
    public CoreException() {
    }

}
