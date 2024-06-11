package org.fat;

import java.util.Date;

public class FileFAT {
    private String name;
    private String extension;

    /*
    Valores que puede tomar el atributo attribute en hex:
        solo_lectura = 0x01
        oculto = 0x02, sistema = 0x04
        etiqueta_volumen = 0x08
        subdirectorio = 0x10 <--- NO APLICA PARA ARCHIVOS
        archivado = 0x20
        reservado = 0x40 o 0x80
    */
    private String attribute;
    private Date creationDate;
    private Date modificationDate;
    private Date lastAccessDate;
    private int size;
    private String content;

    // Constructor default
    public FileFAT() {
        this.name = "filetext";
        this.extension = "txt";
        this.creationDate = new Date();
        this.attribute = "32"; // Archivado
        this.modificationDate = new Date();
        this.size = 32000;
        this.lastAccessDate = new Date();
        this.content = "Archivo auto generado sin ningun parametro inicial.";
    }

    // Constructor con parametros iniciales.
    public FileFAT(String name, String extension, Date creationDate, String attribute, int size, String content) {
        this.name = (name.length() <= 8)? name:"";
        this.extension = (extension.length() <= 3)?extension:"";
        this.creationDate = creationDate;
        this.attribute = attribute;
        this.modificationDate = creationDate;
        this.size = size;
        this.lastAccessDate = creationDate;
        this.content = content;
    }

    public void abrirFile (){
        this.lastAccessDate = new Date();
    }

    public void modifiedFile (String content){
        this.modificationDate = new Date();
        this.content = content;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public String getAttribute() {
        return attribute;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public Date getLastAccessDate() {
        return lastAccessDate;
    }

    public String getContent() {
        return content;
    }

    public int getSize() {
        return size;
    }

    public void setLastAccessDate(Date lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
