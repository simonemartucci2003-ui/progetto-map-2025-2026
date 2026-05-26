/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.client.view;

/**
 * Rappresenta un'area rettangolare cliccabile sullo scenario.
 */
public class BoundingBox {
    private final int minX, maxX, minY, maxY;
    private final String targetName;

    public BoundingBox(int minX, int maxX, int minY, int maxY, String targetName) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.targetName = targetName;
    }

    /**
     * Verifica se il click (X, Y) è caduto dentro questo rettangolo.
     */
    public boolean contiene(int x, int y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    public String getTargetName() {
        return targetName;
    }
}
