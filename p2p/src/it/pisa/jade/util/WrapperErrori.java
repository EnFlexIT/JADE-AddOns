/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/
package it.pisa.jade.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Questa classe salva gli eventuali errori verificatesi durante l'esecuzione di
 * una qualche applicazione e li salva in un file <code>wrap.txt</code>
 * 
 * @author Domenico Trimboli
 * 
 */
public class WrapperErrori {
	/**
	 * file's length in MB
	 */
	private static final int MAX_LENGTH_FILE = 10;

	private static WrapperErrori istanza = new WrapperErrori();

	public static final String messaggioIniziSessione = "inizio nuova sessione";

	public static final String nomeFile = "wrap.txt";

	private static WrapperErrori dammiIstanza() {
		if (istanza == null)
			istanza = new WrapperErrori();
		return istanza;
	}

	@Override
	protected void finalize() throws Throwable {
		out.close();
	}

	public static synchronized void wrap(String msg, Exception e) {
		String messaggio = msg
				+ ((e == null) ? "." : "=" + e.getLocalizedMessage());
		dammiIstanza().println(messaggio);
		if (e != null) {
			StackTraceElement[] stack = e.getStackTrace();
			int i;
			for (i = 0; i < stack.length && i <= 20; i++) {
				dammiIstanza().printlnt(stack[i].toString());
			}
			if (i < stack.length) {
				dammiIstanza().printlnt("più di 20.");
			}
		}
	}

	private SimpleDateFormat formattoreData = new SimpleDateFormat(
			"dd/MMM/yyyy:HH:mm:ss");

	private PrintWriter out;

	private WrapperErrori() {
		inizializza();
	}

	public void println(String msg) {
		out.println(formattoreData.format(new Date()) + ":" + msg);
	}

	public void printlnt(String msg) {
		out.println("\t" + msg);
	}

	private void inizializza() {
		File file = new File(nomeFile);
		if(file.exists()&&file.length()>=(MAX_LENGTH_FILE*1024*1024)){
			file.delete();
		}
		if (file.exists()) {

			try {
				out = new PrintWriter(new FileWriter(file, true), true);
			} catch (IOException e) {
				file.delete();
				try {
					file.createNewFile();
					out = new PrintWriter(new FileWriter(file));
				} catch (IOException e1) {
					System.out
							.println("Impossibile creare il file per effettuare il salvataggio degli errori");
					e1.printStackTrace();
				}
			}
		} else {
			try {
				out = new PrintWriter(new FileWriter(file), true);
			} catch (IOException e) {
				System.out
						.println("Impossibile creare il file per effettuare il salvataggio degli errori");
				e.printStackTrace();
			}
		}
		if (out != null) {
			println(messaggioIniziSessione);
		}
	}

}
