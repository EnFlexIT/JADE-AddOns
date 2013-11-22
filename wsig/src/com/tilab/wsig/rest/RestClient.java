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

package com.tilab.wsig.rest;

import java.io.*;
import java.net.*;

public class RestClient {

	public static String sendFileMessage(String Uri, String fileName,  String accept, String ContentType) {

		// Read file
		ByteArrayOutputStream bout = null;
		try {
			FileInputStream fin = new FileInputStream(fileName);
			bout = new ByteArrayOutputStream();
			copy(fin,bout);
			fin.close();
		} catch(Exception e) {
			String resp = "Error reading file";
			System.out.println(resp);
			e.printStackTrace();
			return resp; 
		}

		// Convert in byte array
		byte[] byteMessage = bout.toByteArray();
		System.out.println("REST Body request:");
		System.out.println(new String(byteMessage));

		// Send message
		return sendMessage(Uri, byteMessage, accept, ContentType);
	}

	public static String sendStringMessage(String Uri, String requestBody,  String accept, String ContentType) {

		// Convert in byte array
		byte[] byteMessage = requestBody.getBytes();

		// Send message
		return sendMessage(Uri, byteMessage, accept, ContentType);
	}

	private static String sendMessage(String Uri, byte[] byteMessage,  String accept, String ContentType) {
		
		String resp = null;
		boolean requestSent = false;
		HttpURLConnection httpConn = null;
		try {
			// Create the connection
			URL url = new URL(Uri);
			URLConnection connection = url.openConnection();
			httpConn = (HttpURLConnection) connection;

			// Set the appropriate HTTP parameters.
			httpConn.setRequestProperty( "Content-Length", String.valueOf( byteMessage.length ) );
			httpConn.setRequestProperty("Content-Type", ContentType);	
			httpConn.setRequestProperty("Accept", accept);	
			httpConn.setRequestMethod( "POST" );
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);

			// Send the rest message
			OutputStream out = httpConn.getOutputStream();
			out.write(byteMessage);    
			out.close();

			requestSent = true;
			
			// Check response code
			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				
				// Read the response
				InputStreamReader isr =	new InputStreamReader(httpConn.getInputStream());
				BufferedReader in = new BufferedReader(isr);

				String inputLine;
				StringBuffer sb = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					sb.append(inputLine);
				}

				in.close();

				resp = sb.toString();
			} else {

				// Get error message
				resp = httpConn.getResponseMessage();
			}
		} catch(Exception e) {
			resp = (requestSent ? "Error response received" : "Error sending REST body request")+" - "+e.getMessage();
			System.out.println(resp);
			e.printStackTrace();
		} finally {
			try { httpConn.disconnect(); } catch(Exception e) {}
		}

		return resp;
	}

	private static void copy(InputStream in, OutputStream out) throws IOException {
		synchronized (in) {
			synchronized (out) {
				byte[] buffer = new byte[256];
				while (true) {
					int bytesRead = in.read(buffer);
					if (bytesRead == -1) break;
					out.write(buffer, 0, bytesRead);
				}
			}
		}
	} 

	public static void main(String[] args) throws Exception {
		
		System.out.println("REST Client to inwoke a REST service");
		System.out.println("");
		
		if (args == null || args.length < 2) {
			System.out.println("usage: RESTClient URL FILE");
			System.out.println("where:");
			System.out.println("URL: rest web-server url");
			System.out.println("FILE: file with rest body request");
			return;
		}
		
		String Uri = args[0];
		String File =args[1]; 
		String accept = args[2];
		String ContentType =args[3]; 
		System.out.println("Web-REST-service: "+Uri);
		String resp = RestClient.sendFileMessage(Uri, File, accept, ContentType);

		System.out.println("");
		System.out.println("REST response:");
		System.out.println(resp);
	}
}
