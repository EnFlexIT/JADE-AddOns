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
 * InvalidMethodCallException is intended for use only in methods that
 * you expect never to be called -- for instance, in the prev() method
 * of the head node in a sequence.  Therefore, only a bug will cause
 * one of these to be actually created and thrown.
 *
 * @author Mark Handy (mdh)
 * @version JDSL 2.1 
 */
public class InvalidMethodCallException extends CoreException {
  public InvalidMethodCallException (String message) {
    super (message);
  }
}
