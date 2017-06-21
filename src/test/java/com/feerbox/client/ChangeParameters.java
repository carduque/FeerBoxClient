package com.feerbox.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Scanner;

public class ChangeParameters {

	public static void main(String[] args) throws IOException {
		String clau, valor, valorFitxer;
		Scanner sc = new Scanner(System.in);
		Properties prop = new Properties();
		InputStream input = null;
		OutputStream output = null;
		String nomFitxer = "fitxer.properties";

		System.out.println("Escriu la clau");
		clau = sc.nextLine();
		System.out.println("Escriu el seu valor");
		valor = sc.nextLine();

		File fitxer = new File(nomFitxer);
		if (!fitxer.exists()) {
			fitxer.createNewFile();
		}

		if (valor.isEmpty() || valor.equals("")) {
			try {

				input = new FileInputStream(nomFitxer);

				prop.load(input);

				valorFitxer = prop.getProperty(clau);

				if (valorFitxer != null) {

					System.out.println("Clau: " + clau + " Valor: " + valorFitxer);

				} else {
					System.out.println("Encara no existeix aquesta key");
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			try {

				input = new FileInputStream(nomFitxer);

				prop.load(input);

				valorFitxer = prop.getProperty(clau);

				if (valorFitxer != null) {
					try {

						output = new FileOutputStream(nomFitxer);
						prop.setProperty(clau, valor);
						prop.store(output, null);
						System.out.println("S'ha actualitzat el valor del Key: " + clau + " Valor antic: " + valorFitxer
								+ " Valor nou: " + valor);

					} catch (IOException io) {
						io.printStackTrace();
					} finally {
						if (output != null) {
							try {
								output.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}

				} else {
					try {

						output = new FileOutputStream(nomFitxer);
						prop.setProperty(clau, valor);
						prop.store(output, null);
						System.out.println("Key creada amb valor: " + valor);

					} catch (IOException io) {
						io.printStackTrace();
					} finally {
						if (output != null) {
							try {
								output.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		sc.close();
	}

}
