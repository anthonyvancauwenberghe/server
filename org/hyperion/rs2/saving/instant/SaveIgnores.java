package org.hyperion.rs2.saving.instant;

import org.hyperion.rs2.model.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SaveIgnores extends SaveObject {

    public SaveIgnores(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean save(final Player player, final BufferedWriter writer) throws IOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void load(final Player player, final String values, final BufferedReader reader) throws IOException {
        // TODO Auto-generated method stub

    }

}
