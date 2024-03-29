/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

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
package jade.webservice.utils;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This class provide various static methods that relax X509 certificate and
 * hostname verification while using the SSL over the HTTP protocol.
 *
 * @author    Francis Labrie
 */
public final class SSLUtils {

	/**
	 * Hostname verifier for the Sun's deprecated API.
	 *
	 * @deprecated see {@link #_hostnameVerifier}.
	 */
	private static com.sun.net.ssl.HostnameVerifier __hostnameVerifier;
	/**
	 * Thrust managers for the Sun's deprecated API.
	 *
	 * @deprecated see {@link #_trustManagers}.
	 */
	private static com.sun.net.ssl.TrustManager[] __trustManagers;
	/**
	 * Hostname verifier.
	 */
	private static HostnameVerifier _hostnameVerifier;
	/**
	 * Thrust managers.
	 */
	private static TrustManager[] _trustManagers;

	private static Object defaultSSLSocketFactory;
	private static Object defaultHostnameVerifier;

	static {
		
		if(isDeprecatedSSLProtocol()) {
			defaultSSLSocketFactory = com.sun.net.ssl.HttpsURLConnection.getDefaultSSLSocketFactory();
			defaultHostnameVerifier = com.sun.net.ssl.HttpsURLConnection.getDefaultHostnameVerifier();
		} else {
			defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
			defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
		}
	}
	
	/**
	 * Set the default Hostname Verifier to an instance of a fake class that
	 * trust all hostnames. This method uses the old deprecated API from the
	 * <code>com.sun.ssl</code> package.
	 *
	 * @deprecated see {@link #_trustAllHostnames()}.
	 */
	private static void __trustAllHostnames() {
		// Create a trust manager that does not validate certificate chains
		if(__hostnameVerifier == null) {
			__hostnameVerifier = new _FakeHostnameVerifier();
		} // if
		// Install the all-trusting host name verifier
		com.sun.net.ssl.HttpsURLConnection.
		setDefaultHostnameVerifier(__hostnameVerifier);
	} // __trustAllHttpsCertificates

	/**
	 * Set the default X509 Trust Manager to an instance of a fake class that
	 * trust all certificates, even the self-signed ones. This method uses the
	 * old deprecated API from the <code>com.sun.ssl</code> package.
	 *
	 * @deprecated see {@link #_trustAllHttpsCertificates()}.
	 */
	private static void __trustAllHttpsCertificates() {
		com.sun.net.ssl.SSLContext context;

		// Create a trust manager that does not validate certificate chains
		if(__trustManagers == null) {
			__trustManagers = new com.sun.net.ssl.TrustManager[]
			                                                   {new _FakeX509TrustManager()};
		} // if
		// Install the all-trusting trust manager
		try {
			context = com.sun.net.ssl.SSLContext.getInstance("SSL");
			context.init(null, __trustManagers, new SecureRandom());
		} catch(GeneralSecurityException gse) {
			throw new IllegalStateException(gse.getMessage());
		} // catch
		com.sun.net.ssl.HttpsURLConnection.
		setDefaultSSLSocketFactory(context.getSocketFactory());
	} // __trustAllHttpsCertificates

	/**
	 * Return <code>true</code> if the protocol handler property <code>java.
	 * protocol.handler.pkgs</code> is set to the Sun's <code>com.sun.net.ssl.
	 * internal.www.protocol</code> deprecated one, <code>false</code>
	 * otherwise.
	 *
	 * @return                <code>true</code> if the protocol handler
	 * property is set to the Sun's deprecated one, <code>false</code>
	 * otherwise.
	 */
	private static boolean isDeprecatedSSLProtocol() {
		return("com.sun.net.ssl.internal.www.protocol".equals(System.
				getProperty("java.protocol.handler.pkgs")));
	} // isDeprecatedSSLProtocol

	/**
	 * Set the default Hostname Verifier to an instance of a fake class that
	 * trust all hostnames.
	 */
	private static void _trustAllHostnames() {
		// Create a trust manager that does not validate certificate chains
		if(_hostnameVerifier == null) {
			_hostnameVerifier = new FakeHostnameVerifier();
		} // if
		// Install the all-trusting host name verifier:
		HttpsURLConnection.setDefaultHostnameVerifier(_hostnameVerifier);
	} // _trustAllHttpsCertificates

	/**
	 * Set the default X509 Trust Manager to an instance of a fake class that
	 * trust all certificates, even the self-signed ones.
	 */
	private static void _trustAllHttpsCertificates() {
		SSLContext context;

		// Create a trust manager that does not validate certificate chains
		if(_trustManagers == null) {
			_trustManagers = new TrustManager[] {new FakeX509TrustManager()};
		} // if
		// Install the all-trusting trust manager:
		try {
			context = SSLContext.getInstance("SSL");
			context.init(null, _trustManagers, new SecureRandom());
		} catch(GeneralSecurityException gse) {
			throw new IllegalStateException(gse.getMessage());
		} // catch
		HttpsURLConnection.setDefaultSSLSocketFactory(context.
				getSocketFactory());
	} // _trustAllHttpsCertificates

	/**
	 * Set the default Hostname Verifier to an instance of a fake class that
	 * trust all hostnames.
	 */
	public static void trustAllHostnames() {
		// Is the deprecated protocol setted?
		if(isDeprecatedSSLProtocol()) {
			__trustAllHostnames();
		} else {
			_trustAllHostnames();
		} // else
	} // trustAllHostnames

	public static void resetTrustAllHostnames() {
		// Is the deprecated protocol setted?
		if(isDeprecatedSSLProtocol()) {
			com.sun.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((com.sun.net.ssl.HostnameVerifier)defaultHostnameVerifier);
		} else {
			HttpsURLConnection.setDefaultHostnameVerifier((javax.net.ssl.HostnameVerifier)defaultHostnameVerifier);
		}
	}
	
	/**
	 * Set the default X509 Trust Manager to an instance of a fake class that
	 * trust all certificates, even the self-signed ones.
	 */
	public static void trustAllHttpsCertificates() {
		// Is the deprecated protocol setted?
		if(isDeprecatedSSLProtocol()) {
			__trustAllHttpsCertificates();
		} else {
			_trustAllHttpsCertificates();
		} // else
	} // trustAllHttpsCertificates

	public static void resetTrustAllHttpsCertificates() {
		// Is the deprecated protocol setted?
		if(isDeprecatedSSLProtocol()) {
			com.sun.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory((javax.net.ssl.SSLSocketFactory)defaultSSLSocketFactory);
		} else {
			HttpsURLConnection.setDefaultSSLSocketFactory((javax.net.ssl.SSLSocketFactory)defaultSSLSocketFactory);
		}
	}
	
	public static void trustAll() {
		trustAllHostnames();
		trustAllHttpsCertificates();
	}

	public static void resetTrustAll() {
		resetTrustAllHostnames();
		resetTrustAllHttpsCertificates();
	}

	/**
	 * Set the file of the trust-store
	 * 
	 * @param trustStore trust-store file
	 */
	public static void setTrustStore(String trustStore) {
		System.setProperty("javax.net.ssl.trustStore", trustStore);
	}

	/**
	 * Set the password of the trust-store
	 * 
	 * @param trustStorePassword password of trust-store
	 */
	public static void setTrustStorePassword(String trustStorePassword) {
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
	}
	
	
	/**
	 * This class implements a fake hostname verificator, trusting any host
	 * name. This class uses the old deprecated API from the <code>com.sun.
	 * ssl</code> package.
	 *
	 * @author    Francis Labrie
	 *
	 * @deprecated see {@link SSLUtilities.FakeHostnameVerifier}.
	 */
	public static class _FakeHostnameVerifier
	implements com.sun.net.ssl.HostnameVerifier {

		/**
		 * Always return <code>true</code>, indicating that the host name is an
		 * acceptable match with the server's authentication scheme.
		 *
		 * @param hostname        the host name.
		 * @param session         the SSL session used on the connection to
		 * host.
		 * @return                the <code>true</code> boolean value
		 * indicating the host name is trusted.
		 */
		public boolean verify(String hostname, String session) {
			return(true);
		} // verify
	} // _FakeHostnameVerifier


	/**
	 * This class allow any X509 certificates to be used to authenticate the
	 * remote side of a secure socket, including self-signed certificates. This
	 * class uses the old deprecated API from the <code>com.sun.ssl</code>
	 * package.
	 *
	 * @author    Francis Labrie
	 *
	 * @deprecated see {@link SSLUtilities.FakeX509TrustManager}.
	 */
	public static class _FakeX509TrustManager
	implements com.sun.net.ssl.X509TrustManager {

		/**
		 * Empty array of certificate authority certificates.
		 */
		private static final X509Certificate[] _AcceptedIssuers =
			new X509Certificate[] {};


		/**
		 * Always return <code>true</code>, trusting for client SSL
		 * <code>chain</code> peer certificate chain.
		 *
		 * @param chain           the peer certificate chain.
		 * @return                the <code>true</code> boolean value
		 * indicating the chain is trusted.
		 */
		public boolean isClientTrusted(X509Certificate[] chain) {
			return(true);
		} // checkClientTrusted

		/**
		 * Always return <code>true</code>, trusting for server SSL
		 * <code>chain</code> peer certificate chain.
		 *
		 * @param chain           the peer certificate chain.
		 * @return                the <code>true</code> boolean value
		 * indicating the chain is trusted.
		 */
		public boolean isServerTrusted(X509Certificate[] chain) {
			return(true);
		} // checkServerTrusted

		/**
		 * Return an empty array of certificate authority certificates which
		 * are trusted for authenticating peers.
		 *
		 * @return                a empty array of issuer certificates.
		 */
		public X509Certificate[] getAcceptedIssuers() {
			return(_AcceptedIssuers);
		} // getAcceptedIssuers
	} // _FakeX509TrustManager


	/**
	 * This class implements a fake hostname verificator, trusting any host
	 * name.
	 *
	 * @author    Francis Labrie
	 */
	public static class FakeHostnameVerifier implements HostnameVerifier {

		/**
		 * Always return <code>true</code>, indicating that the host name is
		 * an acceptable match with the server's authentication scheme.
		 *
		 * @param hostname        the host name.
		 * @param session         the SSL session used on the connection to
		 * host.
		 * @return                the <code>true</code> boolean value
		 * indicating the host name is trusted.
		 */
		public boolean verify(String hostname,
				javax.net.ssl.SSLSession session) {
			return(true);
		} // verify
	} // FakeHostnameVerifier


	/**
	 * This class allow any X509 certificates to be used to authenticate the
	 * remote side of a secure socket, including self-signed certificates.
	 *
	 * @author    Francis Labrie
	 */
	public static class FakeX509TrustManager implements X509TrustManager {

		/**
		 * Empty array of certificate authority certificates.
		 */
		private static final X509Certificate[] _AcceptedIssuers =
			new X509Certificate[] {};


		/**
		 * Always trust for client SSL <code>chain</code> peer certificate
		 * chain with any <code>authType</code> authentication types.
		 *
		 * @param chain           the peer certificate chain.
		 * @param authType        the authentication type based on the client
		 * certificate.
		 */
		public void checkClientTrusted(X509Certificate[] chain,
				String authType) {
		} // checkClientTrusted

		/**
		 * Always trust for server SSL <code>chain</code> peer certificate
		 * chain with any <code>authType</code> exchange algorithm types.
		 *
		 * @param chain           the peer certificate chain.
		 * @param authType        the key exchange algorithm used.
		 */
		public void checkServerTrusted(X509Certificate[] chain,
				String authType) {
		} // checkServerTrusted

		/**
		 * Return an empty array of certificate authority certificates which
		 * are trusted for authenticating peers.
		 *
		 * @return                a empty array of issuer certificates.
		 */
		public X509Certificate[] getAcceptedIssuers() {
			return(_AcceptedIssuers);
		} // getAcceptedIssuers
	} // FakeX509TrustManager
} // SSLUtilities

