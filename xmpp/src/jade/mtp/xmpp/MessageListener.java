/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A.

The updating of this file to JADE 2.0 has been partially supported by the
IST-1999-10211 LEAP Project

This file refers to parts of the FIPA 99/00 Agent Message Transport
Implementation Copyright (C) 2000, Laboratoire d'Intelligence
Artificielle, Ecole Polytechnique Federale de Lausanne

GNU Lesser General Public License

This library is free software; you can redistribute it sand/or
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

/**
 * MessageListener.java
 *
 * @author Miguel Escriva (mescriva@dsic.upv.es)
 * @author Javier Palanca (jpalanca@dsic.upv.es)
 * @author Computer Technology Group (http://www.dsic.upv.es/users/ia/ia.html)
 * @version 0.1
 */



package jade.mtp.xmpp;

import java.io.StringReader;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;

import jade.domain.FIPAAgentManagement.Envelope;
import jade.mtp.MTPException;
import jade.mtp.InChannel.Dispatcher;
import jade.util.Logger;

public class MessageListener implements PacketListener {
	private XMPPConnection _con;
	private Dispatcher _disp;
	private XMLCodec _parser;
	private boolean _verbose;
	
    static Logger myLogger = Logger.getMyLogger (MessageListener.class.getName ());

	public MessageListener(XMPPConnection con, Dispatcher disp, boolean verbose){
		_con = con;
		_disp = disp;
		_verbose = verbose;
	}
	
	public void finalize(){
		stop();
	}
	
	public void start(){
		PacketFilter filter = new MessageTypeFilter(Message.Type.normal);
		_con.addPacketListener(this, filter);		
	}
	
	public void stop(){
		_con.removePacketListener(this);
	}
	
	public void processPacket(Packet packet){
		Message msg = (Message)packet;
		String payload = msg.getBody();
//		System.out.println(msg.getFrom() + ": " + body);
		myLogger.log (Logger.INFO, "Incoming XMPP packet received from " + msg.getFrom ());
		if (_verbose) {
			myLogger.log (Logger.INFO, "Payload = \"" + payload + "\"");
		}
		PacketExtension ext = msg.getExtension(FipaEnvelopePacketExtension.ELEMENT_NAME, FipaEnvelopePacketExtension.NAMESPACE);
		if (ext == null){
			myLogger.log(Logger.WARNING, "Incoming Message does not contain an Envelope! Abort dispatch");
			return;
		}
		if (_verbose) {
			myLogger.log(Logger.INFO, "Encoded envelope extension found: "+ext);
		}
		
		Envelope env = null;
		try{
			FipaEnvelopePacketExtension fipaext = (FipaEnvelopePacketExtension)ext;
			StringReader sr = new StringReader(fipaext.getEnvelope());
			XMLCodec parser = getParser();
			if (parser != null) {
				env = parser.parse(sr);
				if (_verbose) {
					myLogger.log (Logger.INFO, "Envelope successfully parsed: " + env);
				}
			}
			else {
				myLogger.log(Logger.WARNING, "NO XMLCodec available! Abort dispatch");
				return;
			}
		}
		catch (MTPException mtpe) {
			myLogger.log(Logger.WARNING, "Error parsing envelope! Abort dispatch");
			return;
		}
		
		if (_verbose) {
			myLogger.log (Logger.INFO, "Activating internal delivery ... ");
		}
		synchronized (_disp) {
			_disp.dispatchMessage(env, payload.getBytes());
		}
	}

	private synchronized XMLCodec getParser() {
		if (_parser == null) {
			//org.apache.xerces.parsers.SAXParser
			String parserClass = "org.apache.crimson.parser.XMLReaderImpl";
			try {
				_parser = new XMLCodec(parserClass);
			}
			catch (MTPException mtpe) {
				myLogger.log(Logger.SEVERE, "Cannot create XMLCodec with parser class "+parserClass, mtpe);
			}
		}
		return _parser;
	}
}
