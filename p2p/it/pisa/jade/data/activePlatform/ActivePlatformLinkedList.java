/**
 * 
 */
package it.pisa.jade.data.activePlatform;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Domenico Trimboli
 * 
 */
@SuppressWarnings("serial")
public class ActivePlatformLinkedList implements ActivePlatformStub {

	private List<RecordPlatform> list;

	private boolean inIteration = false;

	/**
	 * costruisce un elenco di piattaforme vuoto
	 * 
	 */
	public ActivePlatformLinkedList() {
		list = Collections.synchronizedList(new LinkedList<RecordPlatform>());

	}

	/**
	 * costruisce un elenco di piattaforme a partire da quello passato come
	 * parametro.
	 * 
	 * @param l
	 */
	private ActivePlatformLinkedList(LinkedList<RecordPlatform> l) {
		list = l;
		// this();
		/*
		 * for (RecordPlatform r : l) { list.add(r); }
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pisa.gestioneServizi.PiattaformeAttiveInterfaccia#piattaformeConnesse()
	 */
	@SuppressWarnings("unchecked")
	public Iterator<RecordPlatform> iterator() {
		setInIteration(true);
		Iterator<RecordPlatform> iterator = new Iteratore<RecordPlatform>(list
				.iterator());
		return iterator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pisa.gestioneServizi.PiattaformeAttiveInterfaccia#rimuoviPiattaforma(pisa.gestioneServizi.RecordPiattaforma)
	 */
	public boolean removePlatform(RecordPlatform record) {
		if (isInIteration())
			throw new UnsupportedOperationException(
					"Orepazione di remove non supportata metre è presente un iterazione");
		return list.remove(record);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pisa.gestioneServizi.PiattaformeAttiveInterfaccia#trova(java.lang.String)
	 */
	public RecordPlatform trova(String nomePiattaforma) {
		// poco efficente
		int ris = list.indexOf(new RecordPlatform(nomePiattaforma, null));
		if (ris != -1)
			return list.get(ris);
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pisa.gestioneServizi.PiattaformeAttiveInterfaccia#aggiungiPiattaforma(pisa.gestioneServizi.RecordPiattaforma)
	 */
	public boolean addPlatform(RecordPlatform record) {
		return list.add(record);
	}

	public int size() {
		return list.size();
	}

	public String toString() {

		return list.toString();
	}

	class Iteratore<E> implements Iterator {
		private Iterator<E> it;

		public Iteratore(Iterator<E> i) {
			it = i;
		}

		public boolean hasNext() {
			if (it.hasNext())
				return true;
			else {
				setInIteration(false);
				return false;
			}
		}

		public E next() {
			return it.next();
		}

		public void remove() {
			throw new UnsupportedOperationException("operazione non consentita");
		}

		@Override
		protected void finalize() throws Throwable {
			setInIteration(false);
			super.finalize();
		}

	}

	public boolean refreshRecord(RecordPlatform record, int ttl) {
		int r = list.indexOf(record);
		if (r != -1) {
			RecordPlatform rec = list.get(r);
			if (ttl < 0)
				rec.setTTL(rec.getTTL() + 1);
			else
				rec.setTTL(ttl);
			return true;
		} else
			return false;
	}

	public boolean contains(RecordPlatform r) {
		return list.contains(r);
	}

	public ActivePlatformStub removeAll(ActivePlatform l) {
		if (isInIteration())
			throw new UnsupportedOperationException(
					"Orepazione di removeAll non supportata metre è presente un iterazione");
		LinkedList<RecordPlatform> temp = new LinkedList<RecordPlatform>(list);
		temp.removeAll(((ActivePlatformLinkedList) l).list);
		list.retainAll(((ActivePlatformLinkedList) l).list);
		return new ActivePlatformLinkedList(temp);
	}

	public void tick() {
		if (isInIteration())
			throw new UnsupportedOperationException(
					"Orepazione di tick non supportata metre è presente un iterazione");
		for (RecordPlatform r : list) {
			r.decTTL();
		}
	}

	@SuppressWarnings("unchecked")
	public Iterator<RecordPlatform> getIterator() {
		setInIteration(true);
		IteratoreSincro<RecordPlatform> iteratoreSincro = new IteratoreSincro<RecordPlatform>(
				list.iterator());
		return iteratoreSincro;
	}

	private synchronized boolean isInIteration() {
		return inIteration;
	}

	private synchronized void setInIteration(boolean inIteration) {
		this.inIteration = inIteration;
	}

	class IteratoreSincro<E> implements Iterator {
		private Iterator<E> it;

		public IteratoreSincro(Iterator<E> i) {
			it = i;
		}

		public boolean hasNext() {
			if (it.hasNext())
				return true;
			else {
				setInIteration(false);
				return false;
			}
		}

		public E next() {
			return it.next();
		}

		public void remove() {
			it.remove();
		}

		@Override
		protected void finalize() throws Throwable {
			setInIteration(false);
			super.finalize();
		}

	}
}
