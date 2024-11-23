package utils;

import helpers.utils.Area;

import helpers.utils.Tile;




public class Areas extends Task {

    
    public static Area FruitstallArea = new Area(
        new Tile(7176, 14182, 0), 
        new Tile(7204, 14170, 0)
    );

    public static Area BankArea = new Area(
        new Tile(7008, 14130, 0), 
        new Tile(6988, 14150, 0)
    );


    public boolean activate() {

        return false;
    }

    @Override
    public boolean execute() {
        
        return false;
    }
}
