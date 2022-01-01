package me.atomiz.codminigame;

import org.bukkit.block.BlockState;
import org.bukkit.entity.ItemFrame;

import java.util.ArrayList;
import java.util.List;

public class Editor {
    List<BlockState> states = new ArrayList<>();
    ItemFrame itemFrame; // added this just for the shops cuz its not made out of blocks
    EditingType type;

    public Editor(EditingType type) {
        this.type = type;
    }
}