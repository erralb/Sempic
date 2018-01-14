/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.model.rdf;

import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 *
 * @author Pierre Blarre <Pierre.Blarre@etu.univ-grenoble-alpes.fr>
 */
public class SempicOnto {

	public static Resource Photo;
	public static Property albumId;
	public static Property ownerId;
	public static String depicts;
	public static String takenBy;
	public static String takenIn;
	public static Resource Animal;
	public static Resource Depiction;
	
	public SempicOnto() {
	}

	public static Resource getPhoto() {
		return Photo;
	}

	public static void setPhoto(Resource Photo) {
		SempicOnto.Photo = Photo;
	}

	public static Property getAlbumId() {
		return albumId;
	}

	public static void setAlbumId(Property albumId) {
		SempicOnto.albumId = albumId;
	}

	public static Property getOwnerId() {
		return ownerId;
	}

	public static void setOwnerId(Property ownerId) {
		SempicOnto.ownerId = ownerId;
	}

	public static String getDepicts() {
		return depicts;
	}

	public static void setDepicts(String depicts) {
		SempicOnto.depicts = depicts;
	}

	public static String getTakenBy() {
		return takenBy;
	}

	public static void setTakenBy(String takenBy) {
		SempicOnto.takenBy = takenBy;
	}

	public static String getTakenIn() {
		return takenIn;
	}

	public static void setTakenIn(String takenIn) {
		SempicOnto.takenIn = takenIn;
	}

	public static Resource getAnimal() {
		return Animal;
	}

	public static void setAnimal(Resource Animal) {
		SempicOnto.Animal = Animal;
	}

	public static Resource getDepiction() {
		return Depiction;
	}

	public static void setDepiction(Resource Depiction) {
		SempicOnto.Depiction = Depiction;
	}
	
}
