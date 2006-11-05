/*****************************************************************JADE - Java Agent DEvelopment Framework is a framework to develop multi-agent systems in compliance with the FIPA specifications.Copyright (C) 2000 CSELT S.p.A. GNU Lesser General Public LicenseThis library is free software; you can redistribute it and/ormodify it under the terms of the GNU Lesser General PublicLicense as published by the Free Software Foundation, version 2.1 of the License. This library is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNULesser General Public License for more details.You should have received a copy of the GNU Lesser General PublicLicense along with this library; if not, write to theFree Software Foundation, Inc., 59 Temple Place - Suite 330,Boston, MA  02111-1307, USA.*****************************************************************/package it.pisa.jade.agents.peerAgent;import it.pisa.jade.agents.peerAgent.data.SearchElements;import it.pisa.jade.agents.peerAgent.gui.FramePeerAgent;import it.pisa.jade.agents.peerAgent.ontologies.Choose;import it.pisa.jade.agents.peerAgent.ontologies.Found;import it.pisa.jade.agents.peerAgent.ontologies.LookFor;import it.pisa.jade.agents.peerAgent.ontologies.PeerOntology;import it.pisa.jade.agents.peerAgent.ontologies.PeerVocabulary;import it.pisa.jade.agents.peerAgent.util.PeerServer;import it.pisa.jade.util.FactoryUtil;import it.pisa.jade.util.Parameter;import it.pisa.jade.util.Values;import it.pisa.jade.util.WrapperErrori;import jade.content.lang.Codec;import jade.content.lang.sl.SLCodec;import jade.content.onto.Ontology;import jade.core.AID;import jade.core.Agent;import jade.core.behaviours.ParallelBehaviour;import jade.domain.DFService;import jade.domain.FIPAException;import jade.domain.FIPAAgentManagement.DFAgentDescription;import jade.domain.FIPAAgentManagement.ServiceDescription;import java.io.File;import java.net.InetAddress;import java.net.UnknownHostException;import java.util.ArrayList;import java.util.HashSet;import java.util.LinkedList;import java.util.Observable;import javax.swing.JFileChooser;/** *  * @author Fabrizio Marozzo * @author Domenico Trimboli * */@SuppressWarnings("serial")public class PeerAgent extends Agent implements PeerVocabulary, Command {	Codec codec = null;	Ontology ontology = null;	MyObservable observable = null;	SearchElements searchElements = null;	HashSet<AID> listPeers = null;	ParallelBehaviour pBehaviour = null;	ArrayList<File> files = new ArrayList<File>();		PeerServer server=null;		private InetAddress addressIp=null;		private int addressPort=-1;	private FramePeerAgent frame;	@Override	protected void setup() {		codec = new SLCodec();		ontology = PeerOntology.getInstance();		searchElements = new SearchElements();		listPeers = new HashSet<AID>();		observable = new MyObservable();		// Register language and ontology		getContentManager().registerLanguage(codec);		getContentManager().registerOntology(ontology);		inizialize();		registerDF();		pBehaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);		pBehaviour.addSubBehaviour(new SearchPeersAgentBehaviour(this,				Parameter.getLong(Values.peerAgentPeriod)));		pBehaviour.addSubBehaviour(new ReceiverBehaviour(this));		this.addBehaviour(pBehaviour);		frame = new FramePeerAgent(this);		searchElements.addObserver(frame);		observable.addObserver(frame);		frame.setVisible(true);		try {			addressIp=InetAddress.getLocalHost();		} catch (UnknownHostException e) {			// TODO Auto-generated catch block			e.printStackTrace();		}		addressPort=Parameter.getInt(Values.peerAddressPort);		server=new PeerServer(addressPort, frame);		server.start();	}	private void inizialize() {		// TODO Auto-generated method stub		String path = Parameter.get(Values.sharedDirectory);				File f = path==null?null:new File(path);		if (path==null || !f.exists() || !f.isDirectory()) {			JFileChooser chooser = new JFileChooser("");			chooser.setDialogTitle("SELECT SHARED DIRECTORY");			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);			int ris = chooser.showOpenDialog(null);			while (ris != JFileChooser.APPROVE_OPTION) {				ris = chooser.showOpenDialog(null);			}			f = chooser.getSelectedFile();			Parameter					.set(Values.sharedDirectory, f.getAbsolutePath());		}		File[] listFiles = f.listFiles();// TODO filtro file sulle directory		if (listFiles != null) {			for (int i = 0; i < listFiles.length; i++) {				files.add(listFiles[i]);			}		}	}	private void registerDF() {		ServiceDescription sd = new ServiceDescription();		sd.setType(TYPE_SERVICE);		sd.setName(NAME_SERVICE);		DFAgentDescription dfd = new DFAgentDescription();		dfd.setName(this.getAID());		dfd.addServices(sd);		try {			DFService.register(this, dfd);			System.out.println(getLocalName() + " is ready.");		} catch (FIPAException e) {			WrapperErrori.wrap("Failed registering with DF! Shutting down...",					e);			doDelete();		}	}	@Override	protected void takeDown() {		frame.dispose();		try {			DFService.deregister(this);		} catch (FIPAException e) {			WrapperErrori.wrap("Failed deregistering with DF! Shutting down...",					e);		}	}	public void startNewSearch(LookFor lookfor) {		lookfor.setSearchString(lookfor.getSearchString().toLowerCase());		pBehaviour.addSubBehaviour(new StartNewSearchBehaviour(this, lookfor));	}	public void chooseFile(String searchKey, int pos) {		LinkedList l=searchElements.getSearchElements(searchKey);		if(l!=null && l.size()>pos){			Found found=(Found) l.get(pos);			Choose choose=new Choose();			choose.setSearchKey(found.getSearchKey());			choose.setUrl(found.getUrl());			choose.setIp(addressIp.getHostAddress());			choose.setPort(addressPort);			AID peerDest=FactoryUtil.createAID(found.getAgent());			pBehaviour.addSubBehaviour(new ManageChoosingFile(this, choose, peerDest));		}					}	public void exitFromGroup() {		doDelete();	}	public void removeSearch(String keyString) {		searchElements.removeSearch(keyString);	}	void notifyObservers() {		observable.setChanged();		observable.notifyObservers(this.listPeers);	}	private class MyObservable extends Observable {		@Override		public synchronized void setChanged() {			// TODO Auto-generated method stub			super.setChanged();		}	}}