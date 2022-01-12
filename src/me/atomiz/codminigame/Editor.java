package me.atomiz.codminigame;

import org.bukkit.Location;
import org.bukkit.block.BlockState;

import java.util.ArrayList;
import java.util.List;

public class Editor {
    List<BlockState> states = new ArrayList<>();
    Location itemFrame;
    EditingType type;

    public Editor(EditingType type) {
        this.type = type;
    }
}