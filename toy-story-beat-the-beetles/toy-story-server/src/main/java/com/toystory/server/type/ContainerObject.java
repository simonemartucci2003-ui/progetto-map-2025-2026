/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un oggetto dello scenario che funge da contenitore (es. Baule, Scrivania).
 * Estende AdvObject e implementa una lista interna per contenere oggetti raccoglibili.
 */
public class ContainerObject extends AdvObject {

    /** Lista di oggetti nascosti o custoditi all'interno di questo contenitore. */
    private final List<PickupableObject> containedObjects = new ArrayList<>();
    // NUOVI ATTRIBUTI: Stato di apertura e di blocco del contenitore
    private boolean open = false;
    private boolean locked = false;

    public ContainerObject(int id, String name, String description) {
        super(id, name, description);
    }
    
    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /** @return La lista degli oggetti contenuti. */
    public List<PickupableObject> getContainedObjects() {
        return containedObjects;
    }

    /**
     * Inserisce un oggetto all'interno del contenitore (es. mette il lazo nella scrivania).
     * @param obj L'oggetto leggero da nascondere dentro.
     */
    public void addContainedObject(PickupableObject obj) {
        this.containedObjects.add(obj);
    }

    /**
     * Rimuove un oggetto dal contenitore (es. quando viene scoperto e preso).
     * @param obj L'oggetto da rimuovere.
     */
    public void removeContainedObject(PickupableObject obj) {
        this.containedObjects.remove(obj);
    }
}